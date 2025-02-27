package com.yoiberdev.uberclone.data.source.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(private val firebaseAuth: FirebaseAuth) {

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Puedes agregar más funciones, como signOut, etc.
    suspend fun signOut() {
        firebaseAuth.signOut()
    }
}