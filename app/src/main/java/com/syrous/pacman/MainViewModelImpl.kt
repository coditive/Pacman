package com.syrous.pacman

import androidx.lifecycle.ViewModel
import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModelImpl : ViewModel(), GameViewModel, GameController {

    override val currentScreen: MutableStateFlow<GameScreen> =
        MutableStateFlow(GameScreen.START_SCREEN)

    override var gameState: PacmanState = PacmanStateImpl()


    override fun startGame() {
        currentScreen.value = GameScreen.GAME_PLAY
    }

    override fun pauseGame() {
        TODO("Not yet implemented")
    }

    override fun resumeGame() {

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