package com.syrous.pacman.model

data class MoveInCage(
    val x: Float,
    val y: Float,
    val direction: Directions,
    val dest: Float,
    val speed: Float
)
