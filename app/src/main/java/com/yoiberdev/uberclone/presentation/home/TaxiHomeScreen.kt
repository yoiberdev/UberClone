package com.yoiberdev.uberclone.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaxiHomeScreen(
    onLogout: () -> Unit,
    onViewRequests: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido, Taxista")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onViewRequests) {
            Text("Ver Solicitudes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}
