package com.syrous.pacman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModelImpl : ViewModel(), GameViewModel, GameController {

    override val currentScreen: MutableStateFlow<GameScreen> =
        MutableStateFlow(GameScreen.START_SCREEN)

    override var gameState: PacmanState = PacmanStateImpl()

    private var gameLoop: Job? = null


    override fun startGame() {
        currentScreen.value = GameScreen.GAME_PLAY
        resumeGame()
    }

    override fun pauseGame() {
        TODO("Not yet implemented")
    }

    override fun resumeGame() {
        gameLoop = viewModelScope.launch {
            while (true) {
                delay(160)
                gameState.updateEnemyPositionAfterLoop()
            }
        }
    }

    override fun endGame() {
        TODO("Not yet implemented")
    }

    override fun moveUp() {
        gameState.moveUp()
    }

    override fun moveLeft() {
        gameState.moveLeft()
    }

    override fun moveRight() {
        gameState.moveRight()
    }

    override fun moveDown() {
        gameState.moveDown()
    }

}