package com.yoiberdev.uberclone.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Por favor completa todos los campos")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("Error al iniciar sesión: ${it.message}")
            }
    }

    fun authenticateWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Crea una credencial de Google con el token de ID
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Autentica con Firebase usando la credencial de Google
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                // Autenticación exitosa
                onSuccess()
            }
            .addOnFailureListener {
                // Fallo en la autenticación
                onError("Error al autenticarse con Google: ${it.message}")
            }
    }

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }
}