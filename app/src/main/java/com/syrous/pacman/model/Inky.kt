package com.syrous.pacman.model

data class Inky(
    override val position: Pair<Float, Float>,
    override val speed: CurrentSpeed,
    override val tilePos: Pair<Int, Int>,
    override val lastGoodTilePos: Pair<Int, Int>,
    override val screenPos: Pair<Float, Float>,
    override val physicalSpeed: Float,
    override val tunnelSpeed: Float,
    override val fullSpeed: Float,
    override val lastActiveDir: Directions,
    override val direction: Directions,
    override val nextDir: Directions,
) : Ghost(
    position,
    speed,
    tilePos,
    physicalSpeed,
    tunnelSpeed,
    fullSpeed,
    lastGoodTilePos,
    screenPos,
    lastActiveDir,
    direction,
    nextDir
)