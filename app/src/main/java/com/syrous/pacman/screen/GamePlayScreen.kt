package com.syrous.pacman.screen

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.syrous.pacman.PacmanRadius
import com.syrous.pacman.PacmanState
import com.syrous.pacman.R
import com.syrous.pacman.UnitScale


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
    fun Screen() {
        Column(modifier = Modifier.fillMaxSize()) {
            val pacmanPosition = gameState.pacman.collectAsState().value
            PacmanPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .border(
                        width = 1.dp,
                        color = Color.Gray
                    ),
                position = pacmanPosition
            )

            PacmanController(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                performAction = performAction
            )
        }
    }

    @Composable
    fun PacmanPlayer(modifier: Modifier = Modifier, position: Pair<Int, Int>) {
        Canvas(modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            gameState.updateScreenDimensions(
                layoutCoordinates.size.width / UnitScale,
                layoutCoordinates.size.height / UnitScale
            )
        }) {
            drawCircle(
                color = Color.Yellow,
                radius = PacmanRadius.dp.toPx(),
                center = Offset(
                    position.first * UnitScale.toFloat(),
                    position.second * UnitScale.toFloat()
                )
            )
        }
    }

    @Composable
    fun PacmanController(
        modifier: Modifier = Modifier,
        performAction: (GamePlayScreenAction) -> Unit
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