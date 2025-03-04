package com.yoiberdev.uberclone.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiRequestsScreen(
    onBack: () -> Unit,
    onRequestSelected: (RideRequest) -> Unit,
    viewModel: TaxiMapViewModel = viewModel() // ViewModel que contiene rideRequests e isLoading
) {
    // Inicia la escucha si aún no se ha iniciado
    viewModel.startListeningRideRequests()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de viaje") },
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
            when {
                viewModel.isLoading.value -> {
                    CircularProgressIndicator()
                }
                viewModel.rideRequests.value.isEmpty() -> {
                    Text("No hay solicitudes guardadas")
                }
                else -> {
                    LazyColumn {
                        items(viewModel.rideRequests.value) { request ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Cliente: ${request.clientName}",
                                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Origen: ${request.origin.latitude}, ${request.origin.longitude}",
                                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Destino: ${request.destination.latitude}, ${request.destination.longitude}",
                                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Button(onClick = { onRequestSelected(request) }) {
                                        Text("Ver Ruta")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
