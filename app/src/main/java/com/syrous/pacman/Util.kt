package com.syrous.pacman


operator fun Pair<Float, Float>.plus(other: Pair<Float, Float>): Pair<Float, Float> {
    return Pair(this.first + other.first, this.second + other.second)
}