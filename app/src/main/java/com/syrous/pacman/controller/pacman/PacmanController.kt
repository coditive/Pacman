package com.syrous.pacman.controller.pacman

import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import kotlinx.coroutines.flow.StateFlow

interface PacmanController {

    val pacman: StateFlow<Pacman>
    fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int)
    fun updatePlayField(playField: Map<Int, Map<Int, Tile>>)
    fun move()
    fun moveLeft()
    fun moveRight()
    fun moveUp()
    fun moveDown()
    fun setFullSpeed(speed: Float)
    fun setDotEatingSpeed(speed: Float)
    fun changeCurrentSpeed()
}