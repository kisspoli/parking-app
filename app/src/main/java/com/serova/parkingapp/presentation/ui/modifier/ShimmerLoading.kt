package com.serova.parkingapp.presentation.ui.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1200,
    gradientWidth: Float = 300f
): Modifier = composed {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = -gradientWidth,
        targetValue = gradientWidth * 3,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )
    )

    this.then(
        Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(translateAnimation, 0f),
                    end = Offset(translateAnimation + gradientWidth, gradientWidth)
                ),
                alpha = 0.9f
            )
    )
}