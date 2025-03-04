package com.yoiberdev.uberclone.presentation.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.android.gms.maps.model.LatLng

class TaxiMapViewModel : ViewModel() {

    // Lista de solicitudes leídas de Firebase
    var rideRequests = mutableStateOf<List<RideRequest>>(emptyList())
        private set

    // Indicador de carga
    var isLoading = mutableStateOf(true)
        private set

    // Referencia a la base de datos en el nodo "ride_requests"
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("ride_requests")

    private val rideRequestsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val list = mutableListOf<RideRequest>()
            for (child in snapshot.children) {
                val request = child.getValue(RideRequest::class.java)
                if (request != null) {
                    // Asigna el ID obtenido de la key del snapshot
                    list.add(request.copy(id = child.key ?: ""))
                }
            }
            rideRequests.value = list
            isLoading.value = false  // Finaliza la carga
        }
        override fun onCancelled(error: DatabaseError) {
            // Maneja el error según convenga
            isLoading.value = false
        }
    }

    fun startListeningRideRequests() {
        dbRef.addValueEventListener(rideRequestsListener)
    }

    fun stopListeningRideRequests() {
        dbRef.removeEventListener(rideRequestsListener)
    }

    fun acceptRideRequest(request: RideRequest) {
        dbRef.child(request.id).child("status").setValue("accepted")
    }

    // Para la ubicación actual del taxista (si la necesitas)
    var currentLatLng = mutableStateOf<LatLng?>(null)
}
