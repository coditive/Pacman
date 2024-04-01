package com.syrous.pacman

import kotlinx.coroutines.flow.StateFlow

interface PacmanState {

    val pacman: StateFlow<Pacman>
    val vWallList: StateFlow<List<Pair<Float, Float>>>
    val hWallList: StateFlow<List<Pair<Float, Float>>>
    val foodList: StateFlow<List<Pair<Int, Int>>>
    val score: StateFlow<Int>
    val enemies: StateFlow<List<Enemy>>
    val isPaused: StateFlow<Boolean>

    fun updateScreenDimensions(width: Int, height: Int)
    fun updatePacmanPositionAfterLoop()
    fun updateEnemyPositionAfterLoop()
    fun pauseGame()
    fun resumeGame()
    fun moveUp()
    fun moveDown()
    fun moveLeft()
    fun moveRight()

}