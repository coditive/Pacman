package com.syrous.pacman.model

data class Tile(
    val isIntersection: Boolean,
    val isPath: Boolean,
    val isTunnel: Boolean,
    val food: Food,
    val allowedDir: Set<Directions> = emptySet(),
)