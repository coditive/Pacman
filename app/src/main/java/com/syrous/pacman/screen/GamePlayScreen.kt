package com.syrous.pacman.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.syrous.pacman.GameState
import com.syrous.pacman.R
import com.syrous.pacman.model.Food
import com.syrous.pacman.util.CutAngle
import com.syrous.pacman.util.EatAngle
import com.syrous.pacman.util.EnergizerRadius
import com.syrous.pacman.util.FoodRadius
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
        val view = LocalView.current
        val windowInsets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)
        val statusBarHeight =
            with(LocalDensity.current) { windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp() }
        val navigationBarHeight = with(LocalDensity.current) {
            windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).top.toDp()
        }

        Initialize()
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = statusBarHeight,
                    bottom = navigationBarHeight + 10.dp
                ),
            topBar = {
                PacmanGameTopBar()
            },
        ) { paddingValues ->
            GameLayout(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, _, _ ->
                            when {
                                pan.x > 0 -> {
                                    performAction(GamePlayScreenAction.MoveRight)
                                }

                                pan.x < 0 -> {
                                    performAction(GamePlayScreenAction.MoveLeft)
                                }

                                pan.y > 0 -> {
                                    performAction(GamePlayScreenAction.MoveDown)
                                }

                                pan.y < 0 -> {
                                    performAction(GamePlayScreenAction.MoveUp)
                                }
                            }
                        }
                    }
            )
        }
    }


    @Composable
    fun PacmanGameTopBar(modifier: Modifier = Modifier) {
        val score = gameState.score.collectAsState().value
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = modifier.wrapContentSize(),
            ) {
                Text(
                    text = "1UP",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Column(
                modifier = Modifier.wrapContentSize(), verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = modifier.size(24.dp))
                Text(
                    text = score.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Column(
                modifier = modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "High Score",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = modifier.size(8.dp))

                Text(
                    text = "16400",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

    @Composable
    fun GameLayout(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                GameWalls(modifier = modifier)
                GamePlayFieldLayout(modifier = modifier)
                GameActorComposable(modifier = modifier)
            }
            GameBottomLayout(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }

    @Composable
    fun GameBottomLayout(modifier: Modifier = Modifier) {
        Row(modifier = modifier.padding(start = 20.dp, end = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            val lives = gameState.lives.collectAsState().value
            Row(modifier = Modifier.wrapContentSize()) {
                for(life in 1..lives) {
                    Image(painter = painterResource(id = R.drawable.pacman), contentDescription = "", modifier = Modifier.size(24.dp))
                }
            }
            Image(painter = painterResource(id = R.drawable.fruit_image), contentDescription = "", modifier = Modifier.size(24.dp))
        }
    }

    @Composable
    fun GameWalls(modifier: Modifier = Modifier) {
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
    fun GameActorComposable(modifier: Modifier = Modifier) {
        val pacman = gameState.pacman.collectAsState().value
        val blinky = gameState.blinky.collectAsState().value
        val pinky = gameState.pinky.collectAsState().value
        val inky = gameState.inky.collectAsState().value
        val clyde = gameState.clyde.collectAsState().value

        val animatableCutAngle = remember {
            Animatable(CutAngle)
        }

        LaunchedEffect(key1 = pacman.tilePos) {
            animatableCutAngle.animateTo(targetValue = CutAngle, animationSpec = keyframes {
                CutAngle at 0 using LinearEasing
                EatAngle at 80 using LinearEasing
                0f at 100 using LinearEasing
                CutAngle at 195
            })
        }

        Canvas(modifier = modifier) {
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
    private fun GamePlayFieldLayout(modifier: Modifier = Modifier) {
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
}
