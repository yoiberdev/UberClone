// TaxiMapViewModel.kt
package com.yoiberdev.uberclone.presentation.map

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaxiMapViewModel : ViewModel() {

    // Ubicación actual del taxista
    var currentLatLng by mutableStateOf<LatLng?>(null)
        private set

    // Lista de RideRequest
    var rideRequests by mutableStateOf<List<RideRequest>>(emptyList())  // <--- Aquí la lista
        private set

    // Para escuchar la ruta "ride_requests" de Firebase
    private val firebaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("ride_requests")

    fun startListeningRideRequests() {
        firebaseRef.addValueEventListener(rideRequestsListener)
    }

    fun stopListeningRideRequests() {
        firebaseRef.removeEventListener(rideRequestsListener)
    }

    private val rideRequestsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newRequests = mutableListOf<RideRequest>()
            for (child in snapshot.children) {
                val request = child.getValue(RideRequest::class.java)
                if (request != null) {
                    newRequests.add(request)
                }
            }
            rideRequests = newRequests
        }

        override fun onCancelled(error: DatabaseError) {
            // Manejar error
        }
    }

    fun acceptRideRequest(request: RideRequest) {
        // Ejemplo: cambiar el estado en la base de datos a 'accepted'
        if (request.id.isNotEmpty()) {
            firebaseRef.child(request.id).child("status").setValue("accepted")
        }
    }

    fun updateCurrentLatLng(latLng: LatLng) {
        currentLatLng = latLng
        // Podrías actualizar Firebase con la ubicación del taxista si quisieras.
    }
}
