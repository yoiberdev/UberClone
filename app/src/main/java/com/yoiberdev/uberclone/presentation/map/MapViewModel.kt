package com.yoiberdev.uberclone.presentation.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    // Estado para la ubicación actual, en texto y coordenadas
    var currentLocation by mutableStateOf("Obteniendo ubicación...")
        private set

    var currentLatLng by mutableStateOf<LatLng?>(null)
        private set

    // Estado para el destino
    var destination by mutableStateOf("")
        private set

    var destinationLatLng: LatLng? by mutableStateOf(null)
        private set

    fun updateCurrentLocation(latLng: LatLng) {
        currentLatLng = latLng
        // Puedes formatear la cadena como prefieras.
        currentLocation = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
    }

    fun updateDestination(newDestination: String) {
        destination = newDestination
    }

    fun setDestination(latLng: LatLng) {
        destinationLatLng = latLng
        destination = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
    }
}
