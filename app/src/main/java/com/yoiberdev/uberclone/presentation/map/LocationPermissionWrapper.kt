package com.yoiberdev.uberclone.presentation.map

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper(
    onBack: () -> Unit
) {
    // Gestionamos el permiso ACCESS_FINE_LOCATION
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    when (locationPermissionState.status) {
        is PermissionStatus.Granted -> {
            // Si el permiso está concedido, mostramos el mapa
            MapScreen(onBack = onBack)
        }
        is PermissionStatus.Denied -> {
            // Si está denegado, lanzamos la solicitud
            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }
            // Mostramos un mensaje de explicación mientras se solicita el permiso.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Se requiere el permiso de ubicación para continuar.")
            }
        }
    }
}
