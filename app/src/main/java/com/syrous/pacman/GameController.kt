package com.syrous.pacman

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