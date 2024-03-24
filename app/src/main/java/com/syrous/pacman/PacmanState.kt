package com.syrous.pacman

import kotlinx.coroutines.flow.MutableStateFlow

class PacmanState {


    private var screenWidth = 0
    private var screenHeight = 0

    val pacman = MutableStateFlow(Pair(0, 0))

    fun updateScreenDimensions(width: Int, height: Int) {
        if (width != screenWidth && height != screenHeight) {
            screenWidth = width
            screenHeight = height
            initializePacman()
        }
    }

    private fun initializePacman() {
        pacman.value = screenWidth / 2 to screenHeight / 2
    }

}