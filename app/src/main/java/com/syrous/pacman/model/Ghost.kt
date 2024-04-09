package com.syrous.pacman.model

import com.syrous.pacman.Directions
import com.syrous.pacman.EnemyModes

data class Ghost (
    val id: Int,
    val position: Pair<Float, Float>,
    val direction: Directions,
    val imageId: Int,
    val enemyMode: EnemyModes
)