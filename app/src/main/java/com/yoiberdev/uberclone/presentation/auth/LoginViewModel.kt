package com.yoiberdev.uberclone.presentation.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoiberdev.uberclone.domain.usecase.LoginWithEmailUseCase
import com.yoiberdev.uberclone.domain.usecase.LoginWithGoogleUseCase
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false
)

class LoginViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    // Actualizar campo email
    fun onEmailChanged(newValue: String) {
        uiState = uiState.copy(email = newValue)
    }

    // Actualizar campo password
    fun onPasswordChanged(newValue: String) {
        uiState = uiState.copy(password = newValue)
    }

    // Mostrar/ocultar password
    fun onTogglePasswordVisibility() {
        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
    }

    // Limpiar mensaje de error
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    // Login con email y password
    fun loginWithEmailAndPassword(
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "loginWithEmailAndPassword: Iniciando login")
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = loginWithEmailUseCase(uiState.email, uiState.password)
            uiState = uiState.copy(isLoading = false)

            if (result.isSuccess) {
                Log.d("LoginViewModel", "loginWithEmailAndPassword: Login exitoso")
                onSuccess()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("LoginViewModel", "loginWithEmailAndPassword: Error - $errorMsg")
                uiState = uiState.copy(errorMessage = errorMsg)
            }
        }
    }

    // Login con Google
    fun loginWithGoogle(
        idToken: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val result = loginWithGoogleUseCase(idToken) // Usa el idToken obtenido
            uiState = uiState.copy(isLoading = false)
            if (result.isSuccess) {
                onSuccess()
            } else {
                uiState = uiState.copy(errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

}
