@file:OptIn(ExperimentalPermissionsApi::class)

package com.yoiberdev.uberclone.presentation.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun RequiredLocationPermissionScreen(
    onPermissionGranted: @Composable () -> Unit
) {
    // Maneja el estado del permiso de ubicación fina
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Chequeamos continuamente el estado del permiso
            when (locationPermissionState.status) {
                is PermissionStatus.Granted -> {
                    // Si el permiso ya fue concedido, renderizamos la UI principal
                    onPermissionGranted()
                }

                is PermissionStatus.Denied -> {
                    // Si está denegado, mostramos un flujo bloqueante hasta que el usuario conceda el permiso
                    LaunchedEffect(Unit) {
                        // Dispara la solicitud de permiso inmediatamente cuando entramos a la pantalla
                        locationPermissionState.launchPermissionRequest()
                    }
                    LocationPermissionRationale(locationPermissionState)
                }
            }
        }
    }
}

@Composable
fun LocationPermissionRationale(
    locationPermissionState: com.google.accompanist.permissions.PermissionState
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "La app requiere tu ubicación para mostrarte en el mapa.")
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Por favor, concede el permiso para continuar.")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Si el usuario denegó permanentemente, lo redirigimos a ajustes
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }) {
            Text("Ir a Ajustes")
        }
    }
}
