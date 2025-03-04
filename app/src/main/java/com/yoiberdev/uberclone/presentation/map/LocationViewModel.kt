package com.yoiberdev.uberclone.presentation.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    // La ubicación se inicializa como null para indicar que aún no se obtuvo
    var currentLocation = mutableStateOf<LatLng?>(null)
        private set

    fun updateLocation(latLng: LatLng) {
        currentLocation.value = latLng
    }
}
