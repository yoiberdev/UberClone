package com.yoiberdev.uberclone.domain.usecase

import android.util.Log
import com.yoiberdev.uberclone.domain.repository.AuthRepository

class LoginWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        Log.d("LoginWithEmailUseCase", "Invocado con email: $email")
        val result = repository.loginWithEmailAndPassword(email, password)
        Log.d("LoginWithEmailUseCase", "Resultado: ${if (result.isSuccess) "Éxito" else "Fallo"}")
        return result
    }
}
