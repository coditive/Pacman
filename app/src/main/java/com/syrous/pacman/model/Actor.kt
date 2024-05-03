package com.syrous.pacman.model


sealed class Actor (
    open val position: Pair<Float, Float>,
    open val tilePos: Pair<Int, Int>,
    open val lastGoodTilePos: Pair<Int, Int>,
    open val screenPos: Pair<Float, Float>,
    open val lastActiveDir: Directions,
    open val direction: Directions,
    open val nextDir: Directions,
)

