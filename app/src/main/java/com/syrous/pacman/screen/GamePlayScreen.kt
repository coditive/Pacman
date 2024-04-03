package com.syrous.pacman.screen

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.syrous.pacman.PacmanState
import com.syrous.pacman.R
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.ui.theme.GameControlActionButtonScheme
import com.syrous.pacman.util.PacmanRadius
import com.syrous.pacman.util.SmallHeight
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.WallHeight
import com.syrous.pacman.util.WallWidth

sealed class GamePlayScreenAction {
    data object MoveUp : GamePlayScreenAction()
    data object MoveLeft : GamePlayScreenAction()
    data object MoveRight : GamePlayScreenAction()
    data object MoveDown : GamePlayScreenAction()
    data object PauseGame : GamePlayScreenAction()
    data object ResumeGame : GamePlayScreenAction()
}

class GamePlay(
    private val gameState: PacmanState,
    private val performAction: (GamePlayScreenAction) -> Unit
) {

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
        val pacmanColor = MaterialTheme.colorScheme.primary
        val wallColor = MaterialTheme.colorScheme.onSurface
        val foodColor = MaterialTheme.colorScheme.secondary

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                val score = gameState.score.collectAsState().value
                Text(text = "Your Score: $score")
            }
        ) { paddingValues ->
            val pacman = gameState.pacman.collectAsState().value
            val vWallList = gameState.vWallList.collectAsState().value
            val hWallList = gameState.hWallList.collectAsState().value
            val foodList = gameState.foodList.collectAsState().value
            val enemies = gameState.ghosts.collectAsState().value
            val isPause = gameState.isPaused.collectAsState().value

            PacmanPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.7f)
                    .border(width = 1.dp, color = Color.Gray)
                    .padding(paddingValues),
                pacman = pacman,
                hWallList = hWallList,
                vWallList = vWallList,
                foodList = foodList,
                enemies = enemies,
                pacmanColor = pacmanColor,
                wallColor = wallColor,
                foodColor = foodColor
            )

            PacmanController(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(paddingValues),
                performAction = performAction,
                pauseState = isPause
            )
        }
    }

    @Composable
    fun PacmanPlayer(
        modifier: Modifier = Modifier,
        pacman: Pacman,
        hWallList: List<Pair<Float, Float>>,
        vWallList: List<Pair<Float, Float>>,
        foodList: List<Pair<Int, Int>>,
        enemies: List<Ghost>,
        pacmanColor: Color,
        wallColor: Color,
        foodColor: Color
    ) {
        val cutAngle = 40f
        val context = LocalContext.current
        val animatableCutAngle = remember {
            Animatable(cutAngle)
        }

        LaunchedEffect(key1 = pacman) {
            animatableCutAngle.animateTo(
                targetValue = cutAngle,
                animationSpec = keyframes {
                    durationMillis = 200
                    cutAngle at 0 using FastOutLinearInEasing
                    0f at 100 using LinearOutSlowInEasing
                    cutAngle at 195
                }
            )
        }

        Canvas(modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                gameState.updateScreenDimensions(
                    coordinates.size.width / UnitScale, coordinates.size.height / UnitScale
                )
            }) {
            drawCircleWithCutout(
                color = pacmanColor,
                radius = PacmanRadius.dp,
                animatedCutAngle = animatableCutAngle.value,
                pacman = pacman
            )

            for (wall in hWallList) {
                drawWall(wall, isVWall = false, wallColor)
            }

            for (wall in vWallList) {
                drawWall(wall, isVWall = true, wallColor)
            }
            for (enemy in enemies) {
                drawEnemy(enemy, context)
            }

            for (food in foodList) {
                drawCircle(
                    color = foodColor,
                    radius = 5.dp.toPx(),
                    center = Offset(
                        food.first * UnitScale.toFloat(),
                        food.second * UnitScale.toFloat()
                    )
                )
            }
        }
    }

    private fun DrawScope.drawEnemy(ghost: Ghost, context: Context) {
        val image = BitmapFactory.decodeResource(context.resources, ghost.imageId).asImageBitmap()
        drawImage(
            image = image,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(image.width, image.height),
            dstOffset = IntOffset(
                ghost.position.first.toInt() * UnitScale,
                ghost.position.second.toInt() * UnitScale
            ),
            dstSize = IntSize(50.dp.toPx().toInt(), 50.dp.toPx().toInt()),
        )
    }

    private fun DrawScope.drawWall(
        position: Pair<Float, Float>,
        isVWall: Boolean,
        wallColor: Color
    ) {
        drawRect(
            color = wallColor,
            topLeft = Offset(
                position.first * UnitScale,
                position.second * UnitScale
            ),
            size = Size(
                if (isVWall) WallWidth * UnitScale.toFloat() else WallHeight * UnitScale.toFloat(),
                if (isVWall) SmallHeight * UnitScale.toFloat() else WallWidth * UnitScale.toFloat()
            )
        )
    }

    private fun DrawScope.drawCircleWithCutout(
        color: Color,
        radius: Dp,
        animatedCutAngle: Float,
        pacman: Pacman
    ) {
        val radiusPx = radius.toPx()
        rotate(
            degrees = pacman.direction.angle,
            pivot = Offset(
                pacman.position.first * UnitScale.toFloat(),
                pacman.position.second * UnitScale.toFloat()
            )
        ) {
            drawArc(
                color = color,
                startAngle = animatedCutAngle,
                sweepAngle = 360f - 2 * animatedCutAngle,
                topLeft = Offset(
                    pacman.position.first * UnitScale - radiusPx,
                    pacman.position.second * UnitScale - radiusPx
                ),
                size = Size(radiusPx * 2, radiusPx * 2),
                useCenter = true,
            )
        }
    }


    @Composable
    fun PacmanController(
        modifier: Modifier = Modifier,
        performAction: (GamePlayScreenAction) -> Unit,
        pauseState: Boolean
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { performAction(GamePlayScreenAction.MoveUp) }) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                        contentDescription = ""
                    )
                }
            }
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { performAction(GamePlayScreenAction.MoveLeft) }) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = ""
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(onClick = { performAction(GamePlayScreenAction.MoveRight) }) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                        contentDescription = ""
                    )
                }
            }
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { performAction(GamePlayScreenAction.MoveDown) }) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
                        contentDescription = ""
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (pauseState) {
                            performAction(GamePlayScreenAction.ResumeGame)
                        } else performAction(GamePlayScreenAction.PauseGame)
                    },
                    colors = GameControlActionButtonScheme
                ) {
                    val string = if (pauseState) "Resume Game" else "Pause Game"
                    Text(text = string)
                }
            }
        }
    }
}