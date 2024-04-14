package com.syrous.pacman.util

import kotlin.math.round


operator fun Pair<Float, Float>.plus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first + other.first, this.second + other.second)
}

fun Pair<Float, Float>.convertFloatToDisplayPos(): Pair<Float, Float> {
    val (pacX, pacY) = this
    val imaginaryX = pacX / UnitScale
    val imaginaryY = pacY / UnitScale
    val newTileX = round(imaginaryX) * UnitScale
    val newTileY = round(imaginaryY) * UnitScale
    return Pair(newTileX, newTileY)
}

fun Pair<Int, Int>.convertIntToDisplayPos(): Pair<Float, Float> {
    val (pacX, pacY) = this
    val imaginaryX = pacX / UnitScale.toFloat()
    val imaginaryY = pacY / UnitScale.toFloat()
    val newTileX = round(imaginaryX) * UnitScale
    val newTileY = round(imaginaryY) * UnitScale
    return Pair(newTileX, newTileY)
}

