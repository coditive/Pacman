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


val PATHS_WITH_FOOD = listOf(
    createVerticalPath(2, 2, 4),
    createVerticalPath(2, 8, 4),
    createHorizontalPath(2, 8, 3),
    createVerticalPath(2, 23, 8),
    createHorizontalPath(2, 5, 6),
    createVerticalPath(4, 5, 4),
    createVerticalPath(7, 5, 26),
    createHorizontalPath(2, 30, 12),
    createHorizontalPath(2, 26, 26),
    createVerticalPath(10, 23, 4),
    createHorizontalPath(2, 23, 6),
    createHorizontalPath(10, 23, 4),
    createVerticalPath(13, 26, 5),
    createHorizontalPath(2, 11, 12),
    createVerticalPath(13, 8, 4),
    createHorizontalPath(7, 8, 7),
    createHorizontalPath(2, 2, 26),
    createVerticalPath(13, 2, 4),
    createHorizontalPath(10, 5, 4),
    createVerticalPath(10, 5, 4),
    createHorizontalPath(16, 23, 4),
    createVerticalPath(19, 23, 4),
    createVerticalPath(16, 26, 5),
    createHorizontalPath(16, 30, 12),
    createVerticalPath(27, 23, 8),
    createVerticalPath(27, 2, 4),
    createHorizontalPath(22, 5, 6),
    createVerticalPath(22, 5, 26),
    createHorizontalPath(22, 23, 6),
    createVerticalPath(25, 5, 4),
    createHorizontalPath(25, 8, 3),
    createVerticalPath(27, 8, 4),
    createHorizontalPath(16, 11, 12),
    createVerticalPath(16, 8, 4),
    createHorizontalPath(16, 8, 7),
    createVerticalPath(16, 2, 4),
    createHorizontalPath(16, 5, 4),
    createVerticalPath(19, 5, 4),
)

val PATH_WITHOUT_FOOD = listOf(
    createVerticalPath(10, 11, 10),
    createVerticalPath(19, 11, 10),
    createHorizontalPath(10, 20, 4),
    createHorizontalPath(16, 20, 4),
    createVerticalPath(13, 20, 4),
    createVerticalPath(16, 20, 4)
)


