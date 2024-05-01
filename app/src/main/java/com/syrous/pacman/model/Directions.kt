package com.syrous.pacman.model

enum class Directions(val move: Pair<Float, Float>, val angle: Float) {
    NONE(Pair(0f, 0f), 0f),
    LEFT(Pair(-1f, 0f), -180f),
    RIGHT(Pair(1f, 0f), 0f),
    UP(Pair(0f, -1f), -90f),
    DOWN(Pair(0f, 1f), 90f),
}

fun Directions.getOppositeDir(): Directions = when (this) {
        Directions.LEFT -> Directions.RIGHT
        Directions.RIGHT -> Directions.LEFT
        Directions.UP -> Directions.DOWN
        Directions.DOWN -> Directions.UP
        Directions.NONE -> Directions.NONE
    }