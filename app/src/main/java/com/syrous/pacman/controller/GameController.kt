package com.syrous.pacman.controller

import com.syrous.pacman.PacmanState

interface GameController {

    val gameState: PacmanState
    fun startGame()
    fun pauseGame()
    fun resumeGame()
    fun endGame()
    fun moveUp()
    fun moveLeft()
    fun moveRight()
    fun moveDown()

}