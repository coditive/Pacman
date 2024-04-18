package com.syrous.pacman.model

enum class Directions(val move: Pair<Float, Float>, val angle: Float) {
    LEFT(Pair(-1f, 0f), -180f),
    RIGHT(Pair(1f, 0f), 0f),
    UP(Pair(0f, -1f), -90f),
    DOWN(Pair(0f, 1f), 90f),
}