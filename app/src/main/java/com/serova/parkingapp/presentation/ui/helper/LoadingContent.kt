package com.serova.parkingapp.presentation.ui.helper

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.serova.parkingapp.presentation.ui.modifier.shimmerLoading

@Composable
fun LoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.CenterStart,
    content: @Composable () -> Unit
) {
    Crossfade(
        targetState = isLoading,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        modifier = modifier
    ) { loading ->
        if (loading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .shimmerLoading()
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .animateContentSize(),
                contentAlignment = contentAlignment
            ) {
                content()
            }
        }
    }
}