package com.syrous.pacman.model

data class Pacman (
    val position: Pair<Float, Float>,
    val previousPosition: Pair<Float, Float>,
    val direction: Directions,
    val previousDirection: Directions
)