package com.syrous.pacman.model

data class Tile(
    val isIntersection: Boolean,
    val isPath: Boolean,
    val isTunnel: Boolean,
    val food: Food,
    val allowedDir: Set<Directions> = emptySet(),
) {
    fun allowedOnlyOpposite(dir: Directions): Boolean =
        allowedDir.contains(dir).not() &&
                allowedDir.contains(dir.getOppositeDir()) &&
                allowedDir.size == 1
}