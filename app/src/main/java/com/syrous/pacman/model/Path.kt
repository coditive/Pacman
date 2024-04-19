package com.syrous.pacman.model

class Path private constructor(
    val x: Int,
    val y: Int,
    val horizontalLength: Int,
    val verticalLength: Int,
    val tunnel: Boolean,
) {
    override fun toString(): String {
        return "Path -> x: $x, y: $y, horizontalLength: $horizontalLength, verticalLength: $verticalLength, tunnel: $tunnel"
    }

    companion object {
        fun createHorizontalPath(x: Int, y: Int, horizontalLength: Int): Path =
            Path(x, y, horizontalLength, 0, tunnel = false)

        fun createVerticalPath(x: Int, y: Int, verticalLength: Int): Path =
            Path(x, y, 0, verticalLength, tunnel = false)

        fun createTunnelPath(x: Int, y: Int, horizontalLength: Int): Path =
            Path(x, y, horizontalLength, 0, tunnel = true)
    }
}