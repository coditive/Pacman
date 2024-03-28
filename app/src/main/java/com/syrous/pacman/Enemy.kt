package com.syrous.pacman

data class Enemy (
    val position: Pair<Float, Float>,
    val direction: Directions,
    val imageId: Int,
    val enemyMode: EnemyModes
)