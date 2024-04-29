package com.syrous.pacman.util

import kotlin.math.pow
import kotlin.math.sqrt


operator fun Pair<Float, Float>.plus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Pair<Float, Float>.minus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first - other.first, this.second - other.second)
}

operator fun Pair<Float, Float>.times(num: Int): Pair<Float, Float> =
    Pair(this.first * num, this.second * num)

fun Pair<Int, Int>.toFloat(): Pair<Float, Float> = Pair(this.first.toFloat(), this.second.toFloat())

fun Pair<Float, Float>.toGamePos(): Pair<Int, Int> =
    Pair(this.first.toInt() / UnitScale, this.second.toInt() / UnitScale)

fun getEuclideanDistanceBetween(
    start: Pair<Float, Float>, target: Pair<Float, Float>
): Float = sqrt((target.first - start.first).pow(2) + (target.second - start.second).pow(2))
