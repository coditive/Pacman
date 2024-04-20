package com.syrous.pacman.util

import com.syrous.pacman.model.Path.Companion.createHorizontalPath
import com.syrous.pacman.model.Path.Companion.createVerticalPath


const val UnitScale = 9
const val WallWidth = 10
const val WallHeight = 20
const val SmallHeight = 15
const val PacmanRadius = 25f
const val FoodRadius = 5f

const val FoodUnitRadius = FoodRadius
const val PacmanUnitRadius = PacmanRadius

const val CutAngle = 40f
const val EatAngle = 60f

const val TileWidth = 9
const val TileHeight = 9

const val NumberOfEnemies = 2
const val GhostSize = 100
const val EnemyChaseSeconds = 7

const val Fraction_1_2 = 0.5f
const val Fraction_1_4 = 0.25f
const val Fraction_3_4 = 0.75f


val PATHS = listOf(
    createVerticalPath(2, 27, 4),
    createVerticalPath(2, 21, 4),
    createHorizontalPath(2, 24, 3),
    createVerticalPath(2, 2, 8),
    createHorizontalPath(2, 27, 6),
    createVerticalPath(4, 24, 4),
    createVerticalPath(7, 2, 26),
    createHorizontalPath(2, 2, 12),
    createHorizontalPath(2, 6, 26),
    createVerticalPath(10, 6, 4),
    createHorizontalPath(2, 9, 6),
    createHorizontalPath(10, 9, 4),
    createVerticalPath(13, 2, 5),
    createHorizontalPath(2, 21, 12),
    createVerticalPath(13, 21, 4),
    createHorizontalPath(7, 24, 7),
    createHorizontalPath(2, 30, 26),
    createVerticalPath(13, 27, 4),
    createHorizontalPath(10, 27, 4),
    createVerticalPath(10, 24, 4),
    createHorizontalPath(16, 9, 4),
    createVerticalPath(19, 6, 4),
    createVerticalPath(16, 2, 5),
    createHorizontalPath(16, 2, 12),
    createVerticalPath(27, 2, 8),
    createVerticalPath(27, 27, 4),
    createHorizontalPath(22, 27, 6),
    createVerticalPath(22, 2, 26),
    createHorizontalPath(22, 9, 6),
    createVerticalPath(25, 24, 4),
    createHorizontalPath(25, 24, 3),
    createVerticalPath(27, 21, 4),
    createHorizontalPath(16, 21, 12),
    createVerticalPath(16, 21, 4),
    createHorizontalPath(16, 24, 7),
    createVerticalPath(16, 27, 4),
    createHorizontalPath(16, 27, 4),
    createVerticalPath(19, 24, 4),
    createVerticalPath(10, 12, 10),
    createVerticalPath(19, 12, 10),
    createHorizontalPath(10, 12, 4),
    createHorizontalPath(16, 12, 4),
    createVerticalPath(13, 9, 4),
    createVerticalPath(16, 9, 4)
    )

val PATH_WITHOUT_FOOD = listOf(
    createVerticalPath(10, 12, 10),
    createVerticalPath(19, 12, 10),
    createHorizontalPath(10, 12, 4),
    createHorizontalPath(16, 12, 4),
    createVerticalPath(13, 9, 4),
    createVerticalPath(16, 9, 4)
)


