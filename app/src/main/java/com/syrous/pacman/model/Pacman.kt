package com.syrous.pacman.model

data class Pacman (
    override val position: Pair<Float, Float>,
    override val tilePos: Pair<Int, Int>,
    override val lastGoodTilePos: Pair<Int, Int>,
    override val screenPos: Pair<Float, Float>,
//    val currentSpeed: CurrentSpeed,
//    val physicalSpeed: Float,
//    val tunnelSpeed: Float,
//    val dotEatingSpeed: Float,
//    val fullSpeed: Float,
    override val lastActiveDir: Directions,
    override val direction: Directions,
    override val nextDir: Directions,
): Actor(position, tilePos, lastGoodTilePos, screenPos, lastActiveDir, direction, nextDir)