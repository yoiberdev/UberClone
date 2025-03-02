package com.yoiberdev.uberclone.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yoiberdev.uberclone.data.source.firebase.FirebaseAuthSource
import com.yoiberdev.uberclone.data.source.remote.GoogleSignInSource
import com.yoiberdev.uberclone.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth, // Ahora es propiedad
    private val googleSignInSource: GoogleSignInSource
) : AuthRepository {

    private val authSource = FirebaseAuthSource(firebaseAuth)

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit> {
        // Delegamos la autenticación al source
        return authSource.signInWithEmailAndPassword(email, password)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            // Ahora firebaseAuth es accesible aquí
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
