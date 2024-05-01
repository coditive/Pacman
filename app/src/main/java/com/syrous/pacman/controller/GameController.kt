package com.syrous.pacman.controller

import com.syrous.pacman.GameState

interface GameController {

    val gameState: GameState
    fun startGame()
    fun pauseGame()
    fun resumeGame()
    fun endGame()
    fun moveUp()
    fun moveLeft()
    fun moveRight()
    fun moveDown()

}