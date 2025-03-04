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
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = viewModel()  // Usamos viewModel() para preservar estado
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        com.google.android.gms.maps.MapsInitializer.initialize(context)
    }
    val coroutineScope = rememberCoroutineScope()

    // Solicita la ubicación actual si no está disponible.
    if (viewModel.currentLatLng == null) {
        LaunchedEffect(Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    viewModel.updateCurrentLocation(latLng)
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Obteniendo ubicación…", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Estado para almacenar la ruta (lista de coordenadas) entre origen y destino.
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Cuando se actualice el destino y tengamos la ubicación actual, obtenemos la ruta.
    LaunchedEffect(viewModel.destinationLatLng, viewModel.currentLatLng) {
        val origin = viewModel.currentLatLng
        val dest = viewModel.destinationLatLng
        if (origin != null && dest != null) {
            routePoints = fetchDirectionsRoute(origin, dest)
        }
    }

    // Configura la cámara con un zoom alto (por ejemplo, 19f) para ver detalles de las calles.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.currentLatLng!!, 19f)
    }

    // Actualiza la cámara cuando cambia la ubicación actual.
    LaunchedEffect(viewModel.currentLatLng) {
        viewModel.currentLatLng?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 19f))
        }
    }

    // Obtén los BitmapDescriptor para tus marcadores personalizados.
    val currentMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_current)
    val destinationMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_destination)

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // Al tocar el mapa se marca el destino.
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
            // Marcador para el destino, si está definido.
            viewModel.destinationLatLng?.let { dest ->
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = dest),
                    title = "Destino",
                    snippet = "Aquí quieres ir",
                    icon = destinationMarkerIcon
                )
            }
            // Dibuja la ruta (polyline) si se obtuvo.
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )
            }
        }

        // Botón de retroceso en la esquina superior izquierda.
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

        // Panel inferior con inputs.
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular Ruta")
            }
        }
    }
}
