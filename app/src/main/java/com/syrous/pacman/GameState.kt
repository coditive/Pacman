package com.syrous.pacman

import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.GameEvent
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.Inky
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Pinky
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface GameState {

    val pacman: StateFlow<Pacman>
    val hWallList: StateFlow<Map<Pair<Float, Float>, Pair<Float, Float>>>
    val vWallList: StateFlow<Map<Pair<Float, Float>, Pair<Float, Float>>>
    val foodList: StateFlow<Map<Int, Map<Int, Food>>>
    val score: StateFlow<Int>
    val blinky: StateFlow<Blinky>
    val pinky: StateFlow<Pinky>
    val inky: StateFlow<Inky>
    val clyde: StateFlow<Clyde>
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
    fun isGhostExitingCage(): Boolean
    fun setGhostExitingCage(ghostExitingCageNow: Boolean)
    fun getMainGhostMain(): GhostMode
    fun getLastMainGhostMode(): GhostMode

}