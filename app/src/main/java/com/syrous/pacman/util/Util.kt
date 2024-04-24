package com.syrous.pacman.util


operator fun Pair<Float, Float>.plus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Pair<Float, Float>.minus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first - other.first, this.second - other.second)
}

operator fun Pair<Float, Float>.times(num: Int): Pair<Float, Float> =
    Pair(this.first * num, this.second * num)

fun Pair<Int, Int>.toFloat(): Pair<Float, Float> = Pair(this.first.toFloat(), this.second.toFloat())