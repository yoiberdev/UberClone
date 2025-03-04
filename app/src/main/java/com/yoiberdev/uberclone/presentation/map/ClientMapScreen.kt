package com.yoiberdev.uberclone.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.yoiberdev.uberclone.R
import com.yoiberdev.uberclone.utils.bitmapDescriptorFromVector
import com.yoiberdev.uberclone.utils.fetchDirectionsRoute
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun ClientMapScreen(
    onBack: () -> Unit,
    onRequestSuccess: () -> Unit,
    viewModel: ClientMapViewModel = viewModel() // ViewModel especializado para la lógica del cliente
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        com.google.android.gms.maps.MapsInitializer.initialize(context)
    }
    val coroutineScope = rememberCoroutineScope()

    // Solicitar la ubicación actual
    if (viewModel.currentLatLng == null) {
        LaunchedEffect(Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.updateCurrentLocation(LatLng(it.latitude, it.longitude))
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Obteniendo ubicación…", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Estado para la ruta (lista de coordenadas) entre origen y destino.
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Calcular la ruta cuando se actualicen los puntos
    LaunchedEffect(viewModel.destinationLatLng, viewModel.currentLatLng) {
        val origin = viewModel.currentLatLng
        val dest = viewModel.destinationLatLng
        if (origin != null && dest != null) {
            routePoints = fetchDirectionsRoute(origin, dest)
        }
    }
    LaunchedEffect(viewModel.taxiRequestSuccess) {
        if (viewModel.taxiRequestSuccess) {
            // Redirige a la pantalla de solicitudes.
            onRequestSuccess() // Este callback se define en el NavGraph y realiza la navegación.
        }
    }
    // Configurar la cámara con zoom para ver detalles.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.currentLatLng!!, 19f)
    }

    // Actualizar la cámara al cambiar la ubicación actual.
    LaunchedEffect(viewModel.currentLatLng) {
        viewModel.currentLatLng?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 19f))
        }
    }

    // Marcadores personalizados
    val currentMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_current)
    val destinationMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_destination)

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // Al tocar el mapa se establece el destino.
                viewModel.setDestination(latLng)
            }
        ) {
            // Marcador para la ubicación actual.
            Marker(
                state = com.google.maps.android.compose.MarkerState(position = viewModel.currentLatLng!!),
                title = "Mi Ubicación",
                snippet = "Aquí estás",
                icon = currentMarkerIcon
            )
            // Marcador para el destino, si se definió.
            viewModel.destinationLatLng?.let { dest ->
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = dest),
                    title = "Destino",
                    snippet = "Lugar seleccionado",
                    icon = destinationMarkerIcon
                )
            }
            // Dibujar la ruta si se obtuvo.
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )
            }
        }

        // Botón de retroceso.
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        // Panel inferior con campos de texto e inputs.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LocationInputField(
                label = "Mi Ubicación",
                value = viewModel.currentLocation,
                onValueChange = { /* No editable */ },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LocationInputField(
                label = "Destino",
                value = viewModel.destination,
                onValueChange = { viewModel.updateDestination(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val origin = viewModel.currentLatLng
                        val dest = viewModel.destinationLatLng
                        if (origin != null && dest != null) {
                            coroutineScope.launch {
                                routePoints = fetchDirectionsRoute(origin, dest)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Calcular Ruta")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.requestTaxi()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Solicitar Taxi")
                    }
                }
            }
        }
    }
}
