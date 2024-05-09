package com.syrous.pacman.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.syrous.pacman.GameState
import com.syrous.pacman.R
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.ui.theme.GameControlActionButtonScheme
import com.syrous.pacman.ui.theme.PressStartFontFamily
import com.syrous.pacman.util.CutAngle
import com.syrous.pacman.util.EatAngle
import com.syrous.pacman.util.EnergizerRadius
import com.syrous.pacman.util.FoodRadius
import com.syrous.pacman.util.GhostSize
import com.syrous.pacman.util.PacmanRadius

sealed class GamePlayScreenAction {
    data object MoveUp : GamePlayScreenAction()
    data object MoveLeft : GamePlayScreenAction()
    data object MoveRight : GamePlayScreenAction()
    data object MoveDown : GamePlayScreenAction()
    data object PauseGame : GamePlayScreenAction()
    data object ResumeGame : GamePlayScreenAction()
}

class GamePlay(
    private val ghostImageList: List<ImageBitmap>,
    private val gameState: GameState,
    private val performAction: (GamePlayScreenAction) -> Unit
) {

    private var pacmanColor: Color? = null
    private var wallColor: Color? = null
    private var foodColor: Color? = null

    @Composable
    fun Initialize() {
        pacmanColor = MaterialTheme.colorScheme.primary
        wallColor = MaterialTheme.colorScheme.onSurface
        foodColor = MaterialTheme.colorScheme.secondary
    }

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
        Initialize()
        Scaffold(modifier = modifier.fillMaxSize(), topBar = {
            val score = gameState.score.collectAsState().value
            Text(text = "Your Score: $score", fontFamily = PressStartFontFamily)
        }) { paddingValues ->

            val isPause = gameState.isPaused.collectAsState().value
            PacmanPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.7f)
                    .border(width = 1.dp, color = Color.Gray)
                    .padding(paddingValues)
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
    fun PacmanPlayer(modifier: Modifier = Modifier) {
        Box(modifier = modifier) {
            PacmanGameWalls(modifier = Modifier.fillMaxSize())
            PacmanScreenLayout(modifier = Modifier.fillMaxSize())
            PacmanActorComposable(modifier = Modifier.fillMaxSize())
        }
    }

    @Composable
    private fun PacmanGameWalls(modifier: Modifier = Modifier) {
        val vGameWalls = gameState.vWallList.collectAsState().value
        val hGameWalls = gameState.hWallList.collectAsState().value
        Canvas(modifier = modifier) {
            for (wall in vGameWalls.keys) {
                val (x1, y1) = wall
                val (x2, y2) = vGameWalls[wall]!!
                drawLine(
                    color = wallColor!!,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 5f
                )
            }

            for (wall in hGameWalls.keys) {
                val (x1, y1) = wall
                val (x2, y2) = hGameWalls[wall]!!
                drawLine(
                    color = wallColor!!,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 5f
                )
            }
        }
    }

    @Composable
    private fun PacmanActorComposable(modifier: Modifier = Modifier) {
        val pacman = gameState.pacman.collectAsState().value
        val blinky = gameState.blinky.collectAsState().value
        val pinky = gameState.pinky.collectAsState().value
        val inky = gameState.inky.collectAsState().value
        val clyde = gameState.clyde.collectAsState().value

        Log.d(
            "GamePlayScreen",
            "pacman -> $pacman, blinky -> $blinky, pinky -> $pinky, inky -> $inky, clyde -> $clyde"
        )

        val animatableCutAngle = remember {
            Animatable(CutAngle)
        }

        LaunchedEffect(key1 = pacman) {
            animatableCutAngle.animateTo(targetValue = CutAngle, animationSpec = keyframes {
                CutAngle at 0 using LinearEasing
                EatAngle at 80 using LinearEasing
                0f at 100 using LinearEasing
                CutAngle at 195
            })
        }

        Canvas(modifier = modifier.onGloballyPositioned { coordinates ->
            gameState.updateScreenDimensions(
                coordinates.size.width, coordinates.size.height
            )
        }) {
            drawCircleWithCutout(
                color = pacmanColor!!,
                radius = PacmanRadius,
                animatedCutAngle = animatableCutAngle.value,
                pacman = pacman
            )

            drawGhost(blinky, ghostImageList[0])
            drawGhost(pinky, ghostImageList[1])
            drawGhost(inky, ghostImageList[2])
            drawGhost(clyde, ghostImageList[3])
        }
    }

    @Composable
    private fun PacmanScreenLayout(modifier: Modifier = Modifier) {
        val foodList = gameState.foodList.collectAsState().value

        Canvas(modifier = modifier.onGloballyPositioned { coordinates ->
            gameState.updateScreenDimensions(
                coordinates.size.width, coordinates.size.height
            )
        }) {
            drawFood(foodList)
        }
    }

    private fun DrawScope.drawFood(foodList: Map<Int, Map<Int, Food>>) {
        for (x in foodList.keys) {
            val col = foodList[x]
            for (y in col!!.keys) {
                drawCircle(
                    color = foodColor!!,
                    radius = if (foodList[x]!![y]!! == Food.PELLET) FoodRadius.dp.toPx() else if (foodList[x]!![y]!! == Food.ENERGIZER) EnergizerRadius.dp.toPx() else 0f,
                    center = Offset(
                        x.toFloat(), y.toFloat()
                    )
                )
            }
        }
    }

    private fun DrawScope.drawGhost(ghost: Ghost, ghostImage: ImageBitmap) {
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

    private fun DrawScope.drawCircleWithCutout(
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
                    }, colors = GameControlActionButtonScheme
                ) {
                    val string = if (pauseState) "Resume Game" else "Pause Game"
                    Text(text = string)
                }
            }
        }
    }
}