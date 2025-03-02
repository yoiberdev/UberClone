package com.yoiberdev.uberclone.data.source.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleSignInSource(private val context: Context) {

    companion object {
        private const val WEB_CLIENT_ID = "109374279695-h4kljjl9lsmpi6fdke409ad58o2gp1vn.apps.googleusercontent.com"
    }

    suspend fun signInWithGoogle(): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
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
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(tokenCredential.idToken)
            } else {
                Result.failure(Exception("Tipo de credencial inesperado: ${credential.type}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
