package com.syrous.pacman

data class Ghost (
    val position: Pair<Float, Float>,
    val direction: Directions,
    val imageId: Int,
    val enemyMode: EnemyModes
)