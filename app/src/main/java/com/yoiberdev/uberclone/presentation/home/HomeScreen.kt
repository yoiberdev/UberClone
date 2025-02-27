package com.yoiberdev.uberclone.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Bienvenido al Home - Aquí irán el chat y el mapa")
        // Aquí integrarás tus componentes de chat y mapa, o navegarás a sus subpantallas.
    }
}