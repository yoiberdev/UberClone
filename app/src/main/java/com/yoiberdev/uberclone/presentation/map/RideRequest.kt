package com.yoiberdev.uberclone.presentation.map

import com.google.android.gms.maps.model.LatLng

/**
 * Data class para mapear las solicitudes que los clientes generan
 * y que el taxista debe visualizar.
 */
data class RideRequest(
    val id: String = "",
    val userId: String = "",
    val clientName: String = "",  // se mapea con Firebase
    val origin: LatLngData = LatLngData(),
    val destination: LatLngData = LatLngData(),
    val status: String = "pending"
) {
    val location: LatLng
        get() = LatLng(origin.latitude, origin.longitude)
}

/**
 * Clase auxiliar para almacenar lat y lng en Firebase.
 */
data class LatLngData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

/**
 * Métodos de extensión para convertir a LatLng
 */
fun RideRequest.getOriginLatLng(): LatLng {
    return LatLng(origin.latitude, origin.longitude)
}
fun RideRequest.getDestinationLatLng(): LatLng {
    return LatLng(destination.latitude, destination.longitude)
}
