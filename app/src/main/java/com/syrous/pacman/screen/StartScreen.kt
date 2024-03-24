package com.syrous.pacman.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class StartScreenAction {
    data object StartGame : StartScreenAction()
    data object EndGame : StartScreenAction()
}

class GameStart(private val performAction: (StartScreenAction) -> Unit) {

    @Composable
    fun Screen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button({ performAction(StartScreenAction.StartGame) }) {
                Text("Start Game")
            }

            Button(onClick = { performAction(StartScreenAction.EndGame) }) {
                Text("End Game")
            }
        }
    }
}