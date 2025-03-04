package com.yoiberdev.uberclone.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientRequestsScreen(
    onBack: () -> Unit,
    viewModel: ClientMapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes") },
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.requests.isEmpty()) {
                Text("No hay solicitudes guardadas")
            } else {
                LazyColumn {
                    items(viewModel.requests) { request ->
                        // Extrae la información de origen y destino
                        val origin = request["origin"] as? Map<*, *>
                        val destination = request["destination"] as? Map<*, *>
                        Text(
                            text = "Origen: ${origin?.get("latitude")}, ${origin?.get("longitude")}\n" +
                                    "Destino: ${destination?.get("latitude")}, ${destination?.get("longitude")}"
                        )
                    }
                }
            }
        }
    }
}
