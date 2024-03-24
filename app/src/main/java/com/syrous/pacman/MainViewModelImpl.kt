package com.syrous.pacman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModelImpl : ViewModel(), GameViewModel, GameController {

    override val currentScreen: MutableStateFlow<GameScreen> =
        MutableStateFlow(GameScreen.START_SCREEN)

    override val gameState: PacmanState = PacmanState()


    override fun startGame() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun moveLeft() {
        TODO("Not yet implemented")
    }

    override fun moveRight() {
        TODO("Not yet implemented")
    }

    override fun moveDown() {
        TODO("Not yet implemented")
    }

}