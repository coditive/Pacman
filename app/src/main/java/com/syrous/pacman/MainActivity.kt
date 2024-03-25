package com.syrous.pacman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.syrous.pacman.navigation.GameScreen
import com.syrous.pacman.screen.GamePlay
import com.syrous.pacman.screen.GamePlayScreenAction
import com.syrous.pacman.screen.GameStart
import com.syrous.pacman.screen.StartScreenAction
import com.syrous.pacman.ui.theme.PacmanTheme

class MainActivity : ComponentActivity() {

    //Screens
    private lateinit var gameStart: GameStart
    private lateinit var gamePlay: GamePlay

    //Controllers
    private val viewModel: GameViewModel by viewModels<MainViewModelImpl>()
    private val controller: GameController by viewModels<MainViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PacmanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoadScreens()
                    val screen = viewModel.currentScreen.collectAsState().value
                    when (screen) {
                        GameScreen.START_SCREEN -> gameStart.Screen()
                        GameScreen.GAME_PLAY -> gamePlay.Screen()
                        GameScreen.GAME_OVER -> TODO()
                    }
                }
            }
        }
    }

    @Composable
    private fun LoadScreens() {
        gameStart = GameStart { action ->
            when (action) {
                StartScreenAction.EndGame -> TODO()
                StartScreenAction.StartGame -> controller.startGame()
            }
        }
        gamePlay = GamePlay(controller.gameState) { action ->
            when (action) {
                GamePlayScreenAction.MoveDown -> TODO()
                GamePlayScreenAction.MoveLeft -> TODO()
                GamePlayScreenAction.MoveRight -> TODO()
                GamePlayScreenAction.MoveUp -> TODO()
            }
        }
    }
}