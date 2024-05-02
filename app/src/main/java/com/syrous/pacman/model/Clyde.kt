package com.syrous.pacman.model

data class Clyde (
    override val position: Pair<Float, Float>,
    override val tilePos: Pair<Int, Int>,
    override val lastGoodTilePos: Pair<Int, Int>,
    override val screenPos: Pair<Float, Float>,
    override val lastActiveDir: Directions,
    override val direction: Directions,
    override val nextDir: Directions,
    ): Ghost(position, tilePos, lastGoodTilePos, screenPos, lastActiveDir, direction, nextDir)