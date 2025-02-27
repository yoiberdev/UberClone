package com.yoiberdev.uberclone.presentation.common.components

import android.credentials.GetCredentialException
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.yoiberdev.uberclone.R
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "109374279695-h4kljjl9lsmpi6fdke409ad58o2gp1vn.apps.googleusercontent.com"
private const val TAG = "LoginScreen"

@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onLoginWithGoogle: (idToken: String) -> Unit
) {
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()

    OutlinedButton (
        onClick = {
            // Desencadenar la lógica de obtener la credencial de Google
            if (!isLoading) {
                coroutineScope.launch {
                    try {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(WEB_CLIENT_ID)
                            .setAutoSelectEnabled(true)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(context, request)
                        val credential = result.credential

                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken
                            // Pasamos el idToken al ViewModel
                            onLoginWithGoogle(idToken)
                        } else {
                            Log.e(TAG, "Tipo de credencial inesperado: ${credential.type}")
                            // Maneja error en UI o logs
                        }
                    } catch (e: GetCredentialException) {
                        // Manejo de errores
                        Log.e(TAG, "Fallo al obtener credencial", e)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Token de Google ID inválido", e)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error general en Google Sign-In", e)
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        enabled = !isLoading
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono de Google
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
