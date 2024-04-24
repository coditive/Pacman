package com.syrous.pacman.model

data class Pacman (
    val position: Pair<Float, Float>,
    val tilePos: Pair<Int, Int>,
    val lastGoodTilePos: Pair<Int, Int>,
    val screenPos: Pair<Float, Float>,
//    val currentSpeed: CurrentSpeed,
//    val physicalSpeed: Float,
//    val tunnelSpeed: Float,
//    val dotEatingSpeed: Float,
//    val fullSpeed: Float,
    val direction: Directions,
)