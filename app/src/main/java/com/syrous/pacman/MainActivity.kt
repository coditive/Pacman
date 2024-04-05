package com.syrous.pacman

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.lifecycleScope
import com.syrous.pacman.controller.GameController
import com.syrous.pacman.navigation.GameScreen
import com.syrous.pacman.screen.GameOver
import com.syrous.pacman.screen.GamePlay
import com.syrous.pacman.screen.GamePlayScreenAction
import com.syrous.pacman.screen.GameStart
import com.syrous.pacman.screen.StartScreenAction
import com.syrous.pacman.ui.theme.PacmanTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //Assets
    private lateinit var ghostImageList: List<ImageBitmap>

    //Screens
    private lateinit var gameStart: GameStart
    private lateinit var gamePlay: GamePlay
    private lateinit var gameOver: GameOver

    //Controllers
    private val viewModel: GameViewModel by viewModels<MainViewModelImpl>()
    private val controller: GameController by viewModels<MainViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ghostImageList = buildList {
            add(BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.ghost_red).asImageBitmap())
            add(BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.ghost_orange).asImageBitmap())
            add(BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.ghost_red).asImageBitmap())
            add(BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.ghost_red).asImageBitmap())
        }


        setContent {
            PacmanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoadScreens()
                    loadListeners()
                    val screen = viewModel.currentScreen.collectAsState().value
                    when (screen) {
                        GameScreen.START_SCREEN -> gameStart.Screen()
                        GameScreen.GAME_PLAY -> gamePlay.Screen()
                        GameScreen.GAME_OVER -> gameOver.Screen()
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
        gamePlay = GamePlay(ghostImageList, controller.gameState) { action ->
            when (action) {
                GamePlayScreenAction.MoveDown -> controller.moveDown()
                GamePlayScreenAction.MoveLeft -> controller.moveLeft()
                GamePlayScreenAction.MoveRight -> controller.moveRight()
                GamePlayScreenAction.MoveUp -> controller.moveUp()
                GamePlayScreenAction.PauseGame -> controller.pauseGame()
                GamePlayScreenAction.ResumeGame -> controller.resumeGame()
            }
        }
        gameOver = GameOver()
    }

    private fun loadListeners() {
        lifecycleScope.launch {
            controller.gameState.gameEvent.collectLatest { event ->
                when (event) {
                    GameEvent.GhostAtePacman -> controller.endGame()
                }
            }
        }
    }
}