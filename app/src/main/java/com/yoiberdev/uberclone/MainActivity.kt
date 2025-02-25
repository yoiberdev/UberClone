package com.yoiberdev.uberclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yoiberdev.uberclone.ui.theme.UberCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UberCloneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FloatingShapesBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Capturamos los colores fuera del lambda de Canvas
    val colorScheme = MaterialTheme.colorScheme

    Canvas(modifier = modifier.fillMaxSize()) {
        // Primer círculo flotante
        val circleBrush1 = Brush.radialGradient(
            colors = listOf(colorScheme.primary, colorScheme.secondary)
        )
        drawCircle(
            brush = circleBrush1,
            radius = size.minDimension * 0.35f,
            center = Offset(x = offset1 * size.width, y = offset1 * size.height)
        )
        // Segundo círculo flotante
        val circleBrush2 = Brush.radialGradient(
            colors = listOf(colorScheme.tertiary, colorScheme.primaryContainer)
        )
        drawCircle(
            brush = circleBrush2,
            radius = size.minDimension * 0.25f,
            center = Offset(x = offset2 * size.width, y = offset2 * size.height)
        )
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    Box(modifier = modifier.fillMaxSize()) {
        FloatingShapesBackground()
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(1000)
                    ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "La mejor app para pedir taxi?",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { /* Acción para login de cliente */ },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                ) {
                    Text(text = "Ingresar como Cliente")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Acción para login de taxista */ },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                    )
                ) {
                    Text(text = "Ingresar como Taxista")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    UberCloneTheme {
        WelcomeScreen()
    }
}
