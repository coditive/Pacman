package com.syrous.pacman.composables

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.syrous.pacman.PacmanRadius
import com.syrous.pacman.PacmanState
import com.syrous.pacman.UnitScale


@Composable
fun PacmanPlayer(
    modifier: Modifier = Modifier,
    animationDuration: Int = 500
) {
    val cutAngle = 40f
    val transitionSpec = remember {
        infiniteRepeatable(
            animation = tween<Float>(
                durationMillis = animationDuration,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    }
    val transitionState = rememberInfiniteTransition(label = "pacman_infinite_transition")
    val animatedCutAngle by transitionState.animateFloat(
        initialValue = 360f - cutAngle,
        targetValue = 360f,
        animationSpec = transitionSpec, label = "pacman_eating_animation"
    )
    Canvas(modifier = modifier) {
        drawCircleWithCutout(50.dp, animatedCutAngle)
    }
}

fun DrawScope.drawCircleWithCutout(radius: Dp, animatedCutAngle: Float) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radiusPx = radius.toPx()

    // Draw the animated circle with cutout
    drawArc(
        color = Color.Red,
        startAngle = 0f,
        sweepAngle = animatedCutAngle,
        topLeft = Offset(center.x - radiusPx, center.y - radiusPx),
        size = Size(radiusPx * 2, radiusPx * 2),
        useCenter = true,
    )
}
