package com.syrous.pacman.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.util.GhostSize

fun DrawScope.drawGhost(ghost: Ghost, ghostImage: ImageBitmap) {
    val scaleFactor = when (ghost.direction) {
        Directions.NONE -> {
            when (ghost.lastActiveDir) {
                Directions.LEFT -> -1f
                else -> 1f
            }
        }

        Directions.LEFT -> -1f

        Directions.RIGHT -> 1f

        Directions.UP -> {
            when (ghost.lastActiveDir) {
                Directions.LEFT -> -1f
                else -> 1f
            }
        }

        Directions.DOWN -> {
            when (ghost.lastActiveDir) {
                Directions.LEFT -> -1f
                else -> 1f
            }
        }
    }
    withTransform(
        transformBlock = {
            translate(0f, 0f)
            scale(
                1f * scaleFactor,
                1f,
                pivot = Offset(GhostSize.toFloat() / 2, GhostSize.toFloat() / 2)
            )
        },
    ) {
        drawImage(
            image = ghostImage,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(ghostImage.width, ghostImage.height),
            dstOffset = IntOffset(
                (ghost.screenPos.first.toInt() - GhostSize / 2) * scaleFactor.toInt(),
                ghost.screenPos.second.toInt() - GhostSize / 2
            ),
            dstSize = IntSize(GhostSize, GhostSize),
        )
    }
}



fun DrawScope.drawCircleWithCutout(
    color: Color, radius: Float, animatedCutAngle: Float, pacman: Pacman
) {
    rotate(
        degrees = pacman.lastActiveDir.angle, pivot = Offset(
            pacman.screenPos.first, pacman.screenPos.second
        )
    ) {
        drawArc(
            color = color,
            startAngle = animatedCutAngle,
            sweepAngle = 360f - 2 * animatedCutAngle,
            topLeft = Offset(
                pacman.screenPos.first - radius, pacman.screenPos.second - radius
            ),
            size = Size(radius * 2, radius * 2),
            useCenter = true,
        )
    }
}