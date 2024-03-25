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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.syrous.pacman.PacmanRadius
import com.syrous.pacman.PacmanState
import com.syrous.pacman.R
import com.syrous.pacman.UnitScale
import com.syrous.pacman.WallHeight
import com.syrous.pacman.WallWidth


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
        Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
            val pacmanPosition = gameState.pacman.collectAsState().value
            val wallList = gameState.wallList.collectAsState().value
            val foodList = gameState.foodList.collectAsState().value

            Log.d("GamePlayScreen", "wallList -> $wallList")

            PacmanPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.7f)
                    .border(width = 1.dp, color = Color.Gray)
                    .padding(paddingValues),
                pacmanPosition = pacmanPosition,
                wallList = wallList,
                foodList = foodList
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
        pacmanPosition: Pair<Int, Int>,
        wallList: List<Pair<Float, Float>>,
        foodList: List<Pair<Int, Int>>
    ) {
//        val cutAngle = 40f
//        val transitionSpec = remember {
//            infiniteRepeatable(
//                animation = tween<Float>(
//                    durationMillis = animationDuration, easing = FastOutLinearInEasing
//                ), repeatMode = RepeatMode.Restart
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
            drawCircleWithCutout(
                PacmanRadius.dp, animatedCutAngle = 0f, Offset(
                    pacmanPosition.first * UnitScale.toFloat(),
                    pacmanPosition.second * UnitScale.toFloat()
                )
            )

            for (wall in wallList) {
                drawWall(wall)
            }
        }
    }

    private fun DrawScope.drawWall(position: Pair<Float, Float>) {
        Log.d("GamePlayScreen", "position -> x->  ${position.first * UnitScale.toFloat()}, y -> ${position.second * UnitScale.toFloat()}")
        val center = position
        drawRect(
            color = Color.Black,
            topLeft = Offset(
                center.first * UnitScale,
                center.second * UnitScale
            ),
            size = Size(
                WallWidth * UnitScale.toFloat(),
                WallHeight * UnitScale.toFloat()
            )
        )
    }

    private fun DrawScope.drawCircleWithCutout(
        radius: Dp, animatedCutAngle: Float, center: Offset
    ) {
        val radiusPx = radius.toPx()
        drawArc(
            color = Color.Red,
            startAngle = 0f,
            sweepAngle = animatedCutAngle,
            topLeft = Offset(center.x - radiusPx, center.y - radiusPx),
            size = Size(radiusPx * 2, radiusPx * 2),
            useCenter = true,
        )
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