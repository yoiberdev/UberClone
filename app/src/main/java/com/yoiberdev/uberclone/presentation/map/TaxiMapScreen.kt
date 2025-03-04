package com.yoiberdev.uberclone.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.yoiberdev.uberclone.R
import com.yoiberdev.uberclone.utils.bitmapDescriptorFromVector
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun TaxiMapScreen(
    onBack: () -> Unit,
    viewModel: TaxiMapViewModel = viewModel() // ViewModel especializado para el taxista
) {
    val context = LocalContext.current

    // Si no se tiene la ubicación actual, se solicita con fusedLocationClient
    if (viewModel.currentLatLng.value == null) {
        LaunchedEffect(Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.currentLatLng.value = LatLng(it.latitude, it.longitude)
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Obteniendo ubicación…", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Configurar la cámara utilizando la ubicación actual del taxista
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.currentLatLng.value!!, 19f)
    }
    LaunchedEffect(viewModel.currentLatLng.value) {
        viewModel.currentLatLng.value?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 19f))
        }
    }

    // Íconos para marcadores
    val currentMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_current)
    val requestMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_destination)

    // Para la lista de solicitudes, accedemos a rideRequests.value
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { /* Para el taxista no se marca destino */ }
        ) {
            // Marcador para la ubicación actual del taxista
            Marker(
                state = com.google.maps.android.compose.MarkerState(position = viewModel.currentLatLng.value!!),
                title = "Mi Ubicación",
                snippet = "Ubicación actual",
                icon = currentMarkerIcon
            )
            // Iteramos sobre las solicitudes guardadas
            viewModel.rideRequests.value.forEach { request ->
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = request.location),
                    title = "Solicitud de ${request.clientName}",
                    snippet = "Toca para aceptar",
                    icon = requestMarkerIcon,
                    onClick = {
                        // Aquí podrías navegar a la pantalla detalle de la solicitud
                        viewModel.acceptRideRequest(request)
                        true
                    }
                )
            }
        }

        // Botón de retroceso para salir de esta pantalla
        IconButton(
            onClick = {
                viewModel.stopListeningRideRequests()
                onBack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
        }

        // Panel inferior: lista de solicitudes de viaje
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Solicitudes de viaje", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(viewModel.rideRequests.value) { request ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = request.clientName, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Origen: ${request.origin.latitude}, ${request.origin.longitude}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Destino: ${request.destination.latitude}, ${request.destination.longitude}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(onClick = { viewModel.acceptRideRequest(request) }) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }
        }
    }
}
