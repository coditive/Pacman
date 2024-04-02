package com.syrous.pacman

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PacmanState {

    val pacman: StateFlow<Pacman>
    val vWallList: StateFlow<List<Pair<Float, Float>>>
    val hWallList: StateFlow<List<Pair<Float, Float>>>
    val foodList: StateFlow<List<Pair<Int, Int>>>
    val score: StateFlow<Int>
    val ghosts: StateFlow<List<Ghost>>
    val isPaused: StateFlow<Boolean>
    val gameEvent: SharedFlow<GameEvent>

    fun updateScreenDimensions(width: Int, height: Int)
    fun updatePacmanPositionAfterLoop()
    suspend fun updateEnemyPositionAfterLoop()
    fun pauseGame()
    fun resumeGame()
    fun moveUp()
    fun moveDown()
    fun moveLeft()
    fun moveRight()

}