package com.yoiberdev.uberclone.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue

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
    val colorScheme = MaterialTheme.colorScheme

    Canvas(modifier = modifier.fillMaxSize()) {
        val circleBrush1 = Brush.radialGradient(
            colors = listOf(colorScheme.primary, colorScheme.secondary)
        )
        drawCircle(
            brush = circleBrush1,
            radius = size.minDimension * 0.35f,
            center = Offset(x = offset1 * size.width, y = offset1 * size.height)
        )
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
