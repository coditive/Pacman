package com.syrous.pacman

import com.syrous.pacman.model.Food
import com.syrous.pacman.model.GameEvent
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PacmanState {

    val pacman: StateFlow<Pacman>
    val playField: Map<Int, Map<Int, Tile>>
    val hWallList: StateFlow<Map<Pair<Float, Float>, Pair<Float, Float>>>
    val vWallList: StateFlow<Map<Pair<Float, Float>, Pair<Float, Float>>>
    val foodList: StateFlow<Map<Int, Map<Int, Food>>>
    val score: StateFlow<Int>
    val ghosts: StateFlow<List<Ghost>>
    val isPaused: StateFlow<Boolean>
    val gameEvent: SharedFlow<GameEvent>

    fun updateScreenDimensions(width: Int, height: Int)
    suspend fun updatePositionAfterLoop()
    fun pauseGame()
    fun resumeGame()
    fun moveUp()
    fun moveDown()
    fun moveLeft()
    fun moveRight()

}