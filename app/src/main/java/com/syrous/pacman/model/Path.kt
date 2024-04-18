package com.syrous.pacman.model

class Path private constructor(
    val x: Int,
    val y: Int,
    val horizontalLength: Int = 0,
    val verticalLength: Int = 0,
    val tunnel: Boolean,
    val isPath: Boolean = true,
    val isIntersection: Boolean = false,
    val allowedDir: Set<Directions> = emptySet()
) {
    companion object {
        fun createHorizontalPath(x: Int, y: Int, horizontalLength: Int): Path =
            Path(x, y, horizontalLength, tunnel = false)

        fun createVerticalPath(x: Int, y: Int, verticalLength: Int): Path =
            Path(x, y, verticalLength, tunnel = false)

        fun createTunnelPath(x: Int, y: Int, horizontalLength: Int): Path =
            Path(x, y, horizontalLength, tunnel = true)
    }
}