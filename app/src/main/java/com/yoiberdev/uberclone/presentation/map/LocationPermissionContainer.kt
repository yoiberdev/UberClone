package com.yoiberdev.uberclone.presentation.map

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionContainer(
    locationViewModel: LocationViewModel = viewModel(),
    // Una vez obtenida la ubicación, podrías navegar o mostrar la pantalla:
    onLocationReady: @Composable (LocationViewModel) -> Unit
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    when (locationPermissionState.status) {
        is PermissionStatus.Granted -> {
            // Si el permiso ya está concedido y no se ha obtenido la ubicación, la solicitamos.
            LaunchedEffect(Unit) {
                if (locationViewModel.currentLocation.value == null) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            locationViewModel.updateLocation(latLng)
                        }
                    }
                }
            }
            // Una vez que se tenga la ubicación (puedes agregar lógica de carga si es necesario)
            onLocationReady(locationViewModel)
        }
        else -> {
            // Mientras no se tenga el permiso, lo solicitamos o mostramos un mensaje
            LaunchedEffect(Unit) { locationPermissionState.launchPermissionRequest() }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Se requiere permiso de ubicación para continuar.")
            }
        }
    }
}
