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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    LaunchedEffect(Unit) {
        com.google.android.gms.maps.MapsInitializer.initialize(context)
        // Inicia la escucha a las solicitudes de viaje en Firebase
        viewModel.startListeningRideRequests()
    }
    val coroutineScope = rememberCoroutineScope()

    // Se requiere una ubicación base para centrar el mapa. Podrías usar la ubicación actual del taxista.
    if (viewModel.currentLatLng == null) {
        // Suponiendo que ya se obtuvo la ubicación o se puede pedir de forma similar
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Obteniendo ubicación…", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Configurar la cámara.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.currentLatLng!!, 19f)
    }

    LaunchedEffect(viewModel.currentLatLng) {
        viewModel.currentLatLng?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 19f))
        }
    }

    // Ícono para la ubicación actual del taxista.
    val currentMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_current)
    // Ícono para marcar la solicitud del cliente.
    val requestMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_destination)

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { /* Para el taxista, no se marca destino */ }
        ) {
            // Marcador para la ubicación actual del taxista.
            Marker(
                state = com.google.maps.android.compose.MarkerState(position = viewModel.currentLatLng!!),
                title = "Mi Ubicación",
                snippet = "Ubicación actual",
                icon = currentMarkerIcon
            )
            // Iterar sobre la lista de solicitudes y colocarlas en el mapa.
            viewModel.rideRequests.forEach { request ->
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = request.location),
                    title = "Solicitud de ${request.clientName}",
                    snippet = "Toca para aceptar",
                    icon = requestMarkerIcon,
                    onClick = {
                        // Al hacer clic, puedes mostrar un diálogo o ejecutar la acción de aceptar la solicitud.
                        viewModel.acceptRideRequest(request)
                        true
                    }
                )
            }
        }

        // Botón de retroceso.
        IconButton(
            onClick = {
                // Detener la escucha de Firebase cuando se abandona la pantalla
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

        // Panel inferior para listar las solicitudes (opcional).
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Solicitudes de viaje", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(viewModel.rideRequests) { request ->
                    Card(modifier = Modifier
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
                                Text(text = "Ubicación: ${request.location.latitude}, ${request.location.longitude}", style = MaterialTheme.typography.bodySmall)
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
