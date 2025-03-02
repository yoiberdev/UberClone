package com.yoiberdev.uberclone.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yoiberdev.uberclone.R
import com.yoiberdev.uberclone.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

// Import para Google ID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

private const val WEB_CLIENT_ID = "109374279695-h4kljjl9lsmpi6fdke409ad58o2gp1vn.apps.googleusercontent.com"
private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    role: String,
    authViewModel: AuthViewModel = AuthViewModel(),
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    // CredentialManager para usar la nueva API
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(600)) +
                slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(600))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de retroceso
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retroceder"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login para $role",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de error
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para login con email y password
            Button(
                onClick = {
                    isLoading = true
                    authViewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            isLoading = false
                            onLoginSuccess()
                        },
                        onError = {
                            isLoading = false
                            errorMessage = it
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Acceder")
                }
            }

            // Separador
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "O",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón "Acceder con Google" (Credential Manager)
            OutlinedButton(
                onClick = {
                    isLoading = true

                    // Configura la opción para GoogleId
                    // filterByAuthorizedAccounts(false) mostrará todas las cuentas
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(WEB_CLIENT_ID)
                        .setAutoSelectEnabled(true) // habilita auto-select si solo hay 1 credencial
                        .build()

                    // Construye la solicitud
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    coroutineScope.launch {
                        try {
                            // Llama a CredentialManager para mostrar la UI
                            val result = credentialManager.getCredential(
                                context = context,
                                request = request
                            )
                            val credential = result.credential

                            // Verificamos si el credential devuelto es un GoogleIdToken
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken

                                // Intercambiar ID Token de Google por credencial de Firebase
                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Autenticación con Firebase exitosa")
                                        onLoginSuccess()
                                    } else {
                                        val error = task.exception?.message ?: "Desconocido"
                                        Log.e(TAG, "Error al autenticar con Firebase: $error")
                                        errorMessage = "Error al autenticar con Google: $error"
                                    }
                                }
                            } else {
                                isLoading = false
                                Log.e(TAG, "Tipo de credencial inesperado: ${credential.type}")
                                errorMessage = "Error: Credencial de Google no encontrada"
                            }
                        } catch (e: GetCredentialException) {
                            isLoading = false
                            Log.e(TAG, "Fallo al obtener credencial", e)
                            errorMessage = "Error al iniciar Google Sign-In: ${e.message}"
                        } catch (e: GoogleIdTokenParsingException) {
                            isLoading = false
                            Log.e(TAG, "Token de Google ID inválido", e)
                            errorMessage = "Error al parsear token de Google"
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e(TAG, "Error general en Google Sign-In", e)
                            errorMessage = "Error al iniciar Google Sign-In: ${e.message}"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Logo oficial de Google
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Acceder con Google")
                }
            }
        }
    }
}
