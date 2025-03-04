package com.yoiberdev.uberclone.presentation.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase

class ClientMapViewModel : ViewModel() {
    // Estado para la ubicación actual (en texto y coordenadas).
    var currentLocation by mutableStateOf("Obteniendo ubicación…")
        private set

    var currentLatLng by mutableStateOf<LatLng?>(null)
        private set

    // Estado para el destino.
    var destination by mutableStateOf("")
        private set

    var destinationLatLng: LatLng? by mutableStateOf(null)
        private set

    var taxiRequestSuccess by mutableStateOf(false)
        private set

    /**
     * Actualiza la ubicación actual del cliente.
     */
    fun updateCurrentLocation(latLng: LatLng) {
        currentLatLng = latLng
        currentLocation = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
    }

    /**
     * Actualiza el texto del destino (por ejemplo, cuando el usuario escribe
     * en el campo de texto manualmente).
     */
    fun updateDestination(newDestination: String) {
        destination = newDestination
    }

    /**
     * Fija el destino al tocar en el mapa (lat, lng).
     */
    fun setDestination(latLng: LatLng) {
        destinationLatLng = latLng
        destination = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
    }
    var requests by mutableStateOf<List<Map<String, Any>>>(emptyList())
        private set

    private fun addRequest(requestData: Map<String, Any>) {
        requests = requests + requestData
    }
    /**
     * Envía la solicitud de taxi a Firebase, junto con la ruta calculada (o al menos el destino).
     * Puedes incluir datos adicionales (hora, ID de usuario, etc.).
     */
    fun requestTaxi() {
        val userId = "userIdEjemplo" // O bien, FirebaseAuth.getInstance().currentUser?.uid ?: "userIdEjemplo"
        val requestData = mapOf(
            "userId" to userId,
            "origin" to (currentLatLng?.toMap() ?: emptyMap<String, Double>()),
            "destination" to (destinationLatLng?.toMap() ?: emptyMap<String, Double>()),
            "status" to "pending"
        )


        val dbRef = FirebaseDatabase.getInstance().getReference("ride_requests")
        val requestId = dbRef.push().key ?: return

        dbRef.child(requestId).setValue(requestData)
            .addOnSuccessListener {
                // Se guardó correctamente; actualizamos el estado local.
                addRequest(requestData)
                taxiRequestSuccess = true
            }
            .addOnFailureListener { error ->
                taxiRequestSuccess = false
            }
    }

}

/**
 * Función de extensión para convertir LatLng a un Map<String, Double>.
 */
fun LatLng.toMap(): Map<String, Double> {
    return mapOf("latitude" to this.latitude, "longitude" to this.longitude)
}
