package com.yoiberdev.uberclone.presentation.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yoiberdev.uberclone.presentation.map.LocationViewModel

@Composable
fun LocationScreen(
    locationViewModel: LocationViewModel
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val location = locationViewModel.currentLocation.value
        if (location != null) {
            Text("Mi ubicación: Lat: ${location.latitude}, Lng: ${location.longitude}")
        } else {
            Text("Obteniendo ubicación...")
        }
    }
}
