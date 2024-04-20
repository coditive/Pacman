package com.syrous.pacman.model

class WallPath private constructor(
    val x: Double,
    val y: Double,
    val horizontalLength: Double,
    val verticalLength: Double,
){
    override fun toString(): String {
        return "Path -> x: $x, y: $y, horizontalLength: $horizontalLength, verticalLength: $verticalLength"
    }

    companion object {
        fun createHorizontalPath(x: Int, y: Int, horizontalLength: Int): WallPath =
            WallPath(x.toDouble(), y.toDouble(), horizontalLength.toDouble(), 0.0)
        fun createHorizontalPath(x: Double, y: Double, horizontalLength: Double): WallPath =
            WallPath(x, y, horizontalLength, 0.0)
        fun createVerticalPath(x: Int, y: Int, verticalLength: Int): WallPath =
            WallPath(x.toDouble(), y.toDouble(), 0.0, verticalLength.toDouble(),)
        fun createVerticalPath(x: Double, y: Double, verticalLength: Double): WallPath =
            WallPath(x, y, 0.0, verticalLength,)

    }
}