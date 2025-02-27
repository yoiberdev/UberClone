package com.yoiberdev.uberclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.yoiberdev.uberclone.navigation.NavGraph
import com.yoiberdev.uberclone.ui.theme.UberCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UberCloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Ahora se utiliza el com.yoiberdev.uberclone.navigation.NavGraph ubicado en presentation.navigation
                    NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
