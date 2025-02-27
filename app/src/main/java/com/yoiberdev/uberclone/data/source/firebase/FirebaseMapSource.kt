package com.yoiberdev.uberclone.data.source.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class LocationUpdate(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

class FirebaseMapSource(private val firestore: FirebaseFirestore) {

    private val locationsCollection = firestore.collection("locations")

    // Enviar o actualizar la ubicación de un usuario
    suspend fun updateLocation(location: LocationUpdate): Result<Unit> {
        return try {
            locationsCollection.document(location.userId).set(location).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Escuchar actualizaciones de ubicaciones (por ejemplo, de taxistas o clientes)
    fun listenForLocations(): Flow<List<LocationUpdate>> = callbackFlow {
        val subscription = locationsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val locations = snapshot?.toObjects(LocationUpdate::class.java) ?: emptyList()
            trySend(locations)
        }
        awaitClose { subscription.remove() }
    }
}