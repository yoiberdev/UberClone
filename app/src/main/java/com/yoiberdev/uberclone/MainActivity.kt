package com.yoiberdev.uberclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.yoiberdev.uberclone.navigation.NavGraph
import com.yoiberdev.uberclone.ui.theme.UberCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase App Check con el proveedor Play Integrity
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        setContent {
            UberCloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Se utiliza el NavGraph ubicado en com.yoiberdev.uberclone.navigation
                    NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
