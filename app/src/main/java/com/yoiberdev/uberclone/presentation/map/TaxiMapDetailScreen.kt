package com.yoiberdev.uberclone.presentation.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.yoiberdev.uberclone.R
import com.yoiberdev.uberclone.utils.bitmapDescriptorFromVector

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun TaxiMapDetailScreen(
    onBack: () -> Unit,
    rideRequest: RideRequest
) {
    // Convertir LatLngData a LatLng
    val origin = LatLng(rideRequest.origin.latitude, rideRequest.origin.longitude)
    val destination = LatLng(rideRequest.destination.latitude, rideRequest.destination.longitude)

    val context = androidx.compose.ui.platform.LocalContext.current
    val currentMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_current)
    val destinationMarkerIcon = bitmapDescriptorFromVector(context, R.drawable.ic_marker_destination)

    // Configurar la cámara para mostrar ambos puntos (por simplicidad se centra en el origen)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(origin, 14f)
    }

    // Opcional: podrías ajustar la cámara para que se vea todo el trayecto usando bounds.
    LaunchedEffect(Unit) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(origin, 14f))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Marcador en el origen
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = origin),
                    title = "Origen",
                    snippet = "Ubicación del cliente",
                    icon = currentMarkerIcon
                )
                // Marcador en el destino
                Marker(
                    state = com.google.maps.android.compose.MarkerState(position = destination),
                    title = "Destino",
                    snippet = "Destino seleccionado",
                    icon = destinationMarkerIcon
                )
            }
        }
    }
}
