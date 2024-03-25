package com.syrous.pacman

import kotlinx.coroutines.flow.StateFlow

interface PacmanState {

    val pacman: StateFlow<Pair<Int, Int>>
    val wallList: StateFlow<List<Pair<Float, Float>>>
    val foodList: StateFlow<List<Pair<Int, Int>>>

    fun updateScreenDimensions(width: Int, height: Int)

}