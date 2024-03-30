package com.syrous.pacman.screen

import android.util.Log
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.syrous.pacman.Enemy
import com.syrous.pacman.Pacman
import com.syrous.pacman.PacmanState
import com.syrous.pacman.R
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
}


class GamePlay(
    private val gameState: PacmanState,
    private val performAction: (GamePlayScreenAction) -> Unit
) {

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
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
            val enemies = gameState.enemies.collectAsState().value

            Log.d("GamePlayScreen", "wallList -> $vWallList & $hWallList")

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
                enemies = enemies
            )

            PacmanController(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(paddingValues),
                performAction = performAction
            )

        }
    }

    @Composable
    fun PacmanPlayer(
        modifier: Modifier = Modifier,
        animationDuration: Int = 500,
        pacman: Pacman,
        hWallList: List<Pair<Float, Float>>,
        vWallList: List<Pair<Float, Float>>,
        foodList: List<Pair<Int, Int>>,
        enemies: List<Enemy>
    ) {
//        val cutAngle = 40f
//        val transitionSpec = remember {
//            infiniteRepeatable(
//                animation = tween<Float>(
//                    durationMillis = animationDuration,
//                    easing = FastOutLinearInEasing
//                ),
//                repeatMode = RepeatMode.Restart
//            )
//        }
//        val transitionState = rememberInfiniteTransition(label = "pacman_infinite_transition")
//        val animatedCutAngle by transitionState.animateFloat(
//            initialValue = 360f - cutAngle,
//            targetValue = 360f,
//            animationSpec = transitionSpec,
//            label = "pacman_eating_animation"
//        )

        Canvas(modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                Log.d("GamePlayScreen", "Canvas coordinates -> ${coordinates.size}")
                gameState.updateScreenDimensions(
                    coordinates.size.width / UnitScale, coordinates.size.height / UnitScale
                )
            }) {
            Log.d("GamePlayScreen", "pacman -> $")
            drawCircleWithCutout(
                PacmanRadius.dp, animatedCutAngle = 310f, pacman

            )

            for (wall in hWallList) {
                drawWall(wall, isVWall = false)
            }

            for (wall in vWallList) {
                drawWall(wall, isVWall = true)
            }
            for (enemy in enemies) {
                drawEnemy(enemy)
            }

            for (food in foodList) {
                drawCircle(
                    color = Color.Black,
                    radius = 5.dp.toPx(),
                    center = Offset(
                        food.first * UnitScale.toFloat(),
                        food.second * UnitScale.toFloat()
                    )
                )
            }
        }
    }

    private fun DrawScope.drawEnemy(enemy: Enemy) {
        drawCircle(
            color = Color.Green,
            radius = 6.dp.toPx(),
            center = Offset(
                enemy.position.first * UnitScale,
                enemy.position.second * UnitScale
            )
        )
    }

    private fun DrawScope.drawWall(position: Pair<Float, Float>, isVWall: Boolean) {
        Log.d(
            "GamePlayScreen",
            "position -> x->  ${position.first * UnitScale.toFloat()}, y -> ${position.second * UnitScale.toFloat()}, isVWall -> $isVWall"
        )
        drawRect(
            color = Color.Black,
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
        radius: Dp, animatedCutAngle: Float, pacman: Pacman
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
                color = Color.Red,
                startAngle = 30f,
                sweepAngle = animatedCutAngle,
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
        modifier: Modifier = Modifier, performAction: (GamePlayScreenAction) -> Unit
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
        }
    }
}