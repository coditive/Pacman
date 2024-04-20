package com.syrous.pacman.util

import com.syrous.pacman.model.Path
import com.syrous.pacman.model.WallPath.Companion.createHorizontalPath
import com.syrous.pacman.model.WallPath.Companion.createVerticalPath


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
    Path.createVerticalPath(2, 27, 4),
    Path.createVerticalPath(2, 21, 4),
    Path.createHorizontalPath(2, 24, 3),
    Path.createVerticalPath(2, 2, 8),
    Path.createHorizontalPath(2, 27, 6),
    Path.createVerticalPath(4, 24, 4),
    Path.createVerticalPath(7, 2, 26),
    Path.createHorizontalPath(2, 2, 12),
    Path.createHorizontalPath(2, 6, 26),
    Path.createVerticalPath(10, 6, 4),
    Path.createHorizontalPath(2, 9, 6),
    Path.createHorizontalPath(10, 9, 4),
    Path.createVerticalPath(13, 2, 5),
    Path.createHorizontalPath(2, 21, 12),
    Path.createVerticalPath(13, 21, 4),
    Path.createHorizontalPath(7, 24, 7),
    Path.createHorizontalPath(2, 30, 26),
    Path.createVerticalPath(13, 27, 4),
    Path.createHorizontalPath(10, 27, 4),
    Path.createVerticalPath(10, 24, 4),
    Path.createHorizontalPath(16, 9, 4),
    Path.createVerticalPath(19, 6, 4),
    Path.createVerticalPath(16, 2, 5),
    Path.createHorizontalPath(16, 2, 12),
    Path.createVerticalPath(27, 2, 8),
    Path.createVerticalPath(27, 27, 4),
    Path.createHorizontalPath(22, 27, 6),
    Path.createVerticalPath(22, 2, 26),
    Path.createHorizontalPath(22, 9, 6),
    Path.createVerticalPath(25, 24, 4),
    Path.createHorizontalPath(25, 24, 3),
    Path.createVerticalPath(27, 21, 4),
    Path.createHorizontalPath(16, 21, 12),
    Path.createVerticalPath(16, 21, 4),
    Path.createHorizontalPath(16, 24, 7),
    Path.createVerticalPath(16, 27, 4),
    Path.createHorizontalPath(16, 27, 4),
    Path.createVerticalPath(19, 24, 4),
    Path.createVerticalPath(10, 12, 10),
    Path.createVerticalPath(19, 12, 10),
    Path.createHorizontalPath(10, 12, 4),
    Path.createHorizontalPath(16, 12, 4),
    Path.createVerticalPath(13, 9, 4),
    Path.createVerticalPath(16, 9, 4),
    Path.createTunnelPath(0, 15, 8),
    Path.createHorizontalPath(7, 15, 4),
    Path.createHorizontalPath(19, 15, 4),
    Path.createTunnelPath(22, 15, 8),
    Path.createHorizontalPath(10, 18, 10),
    Path.createHorizontalPath(13, 12, 4),
    Path.createHorizontalPath(13, 24, 4),
)

val PATH_WITHOUT_FOOD = listOf(
    Path.createVerticalPath(10, 12, 10),
    Path.createVerticalPath(19, 12, 10),
    Path.createHorizontalPath(10, 12, 4),
    Path.createHorizontalPath(16, 12, 4),
    Path.createVerticalPath(13, 9, 4),
    Path.createVerticalPath(16, 9, 4),
    Path.createTunnelPath(0, 15, 8),
    Path.createHorizontalPath(7, 15, 4),
    Path.createHorizontalPath(19, 15, 4),
    Path.createTunnelPath(22, 15, 8),
    Path.createHorizontalPath(10, 18, 10),
    Path.createHorizontalPath(13, 12, 4),
    Path.createHorizontalPath(13, 24, 4),
)


val WALL_LIST = listOf(
    createVerticalPath(1, 1, 10),
    createVerticalPath(1.5, 1.5, 9.0),
    createHorizontalPath(1.5, 9.5, 6.0),
    createVerticalPath(6.5, 9.5, 5.5),
    createHorizontalPath(1, 10, 6),
    createVerticalPath(6.0, 10.0, 4.5),
    createHorizontalPath(0.0, 13.5, 7.0),
    createHorizontalPath(0.0, 14.0, 7.5),
    createHorizontalPath(0.0, 16.0, 7.5),
    createHorizontalPath(0.0, 16.5, 7.0),
    createVerticalPath(6.0, 16.5, 4.5),
    createVerticalPath(6.6, 16.0, 5.5),
    createHorizontalPath(1, 20, 6),
    createHorizontalPath(1.5, 20.5, 6.0),
    createVerticalPath(1, 20, 12),
    createVerticalPath(1.5, 20.5, 5.5),
    createHorizontalPath(1.5, 25.0, 3.0),
    createHorizontalPath(1.5, 26.0, 3.0),
    createVerticalPath(3.5, 25.0, 2.0),
    createVerticalPath(1.5, 26.0, 5.5),
    createVerticalPath(14.0, 1.5, 5.0),
    createVerticalPath(15.0, 1.5, 5.0),

    createVerticalPath(28, 1, 10),
    createVerticalPath(27.5, 1.5, 9.0),
    createVerticalPath(22.5, 9.5, 5.5),
    createVerticalPath(23.0, 10.0, 4.5),
    createVerticalPath(23.0, 16.5, 4.5),
    createVerticalPath(22.5, 16.0, 5.5),
    createVerticalPath(28, 20, 12),
    createVerticalPath(27.5, 20.5, 5.5),
    createVerticalPath(25.5, 25.0, 2.0),
    createVerticalPath(27.5, 26.0, 5.5),
    createHorizontalPath(22.5, 9.5, 6.0),
    createHorizontalPath(23, 10, 6),
    createHorizontalPath(22.5, 14.0, 7.5),
    createHorizontalPath(22.5, 9.5, 6.0),
    createHorizontalPath(23.0, 13.5, 7.0),
    createHorizontalPath(22.5, 16.0, 7.5),
    createHorizontalPath(23.0, 16.5, 7.0),
    createHorizontalPath(22.5, 20.5, 6.0),
    createHorizontalPath(23, 20, 6),
    createHorizontalPath(25.5, 25.0, 3.0),
    createHorizontalPath(25.5, 26.0, 3.0),
    createHorizontalPath(1, 31, 28),
    createHorizontalPath(1.5, 30.5, 27.0),
    createHorizontalPath(1, 1, 28),
    createHorizontalPath(1.5, 1.5, 13.5),
    createHorizontalPath(15.0, 1.5, 13.5),
    createHorizontalPath(14.0, 5.5, 2.0),

    createHorizontalPath(3, 29, 10),
    createHorizontalPath(3, 28, 6),
    createVerticalPath(8, 25, 4),
    createVerticalPath(9, 25, 4),
    createHorizontalPath(9, 28, 4),
    createVerticalPath(3, 28, 2),
    createVerticalPath(12, 28, 2),
    createHorizontalPath(8, 25, 2),


    createHorizontalPath(17, 29, 10),
    createVerticalPath(21, 25, 4),
    createHorizontalPath(21, 28, 6),
    createVerticalPath(20, 25, 4),
    createHorizontalPath(17, 28, 4),
    createVerticalPath(26, 28, 2),
    createVerticalPath(17, 28, 2),
    createHorizontalPath(20, 25, 2),

    createHorizontalPath(3, 22, 4),
    createVerticalPath(3, 22, 2),
    createVerticalPath(6, 22, 5),
    createVerticalPath(5, 23, 4),
    createHorizontalPath(3, 23, 3),
    createHorizontalPath(5, 26, 2),

    createHorizontalPath(23, 22, 4),
    createVerticalPath(23, 22, 5),
    createHorizontalPath(23, 26, 2),
    createVerticalPath(24, 23, 4),
    createHorizontalPath(24, 23, 3),
    createVerticalPath(26, 22, 2),

    createHorizontalPath(11, 25, 8),
    createVerticalPath(14, 26, 4),
    createVerticalPath(15, 26, 4),
    createHorizontalPath(11, 26, 4),
    createHorizontalPath(15, 26, 4),
    createHorizontalPath(14, 29, 2),
    createVerticalPath(11, 25, 2),
    createVerticalPath(18, 25, 2),

    createHorizontalPath(8, 22, 5),
    createHorizontalPath(8, 23, 5),
    createVerticalPath(8, 22, 2),
    createVerticalPath(12, 22, 2),

    createHorizontalPath(17, 22, 5),
    createHorizontalPath(17, 23, 5),
    createVerticalPath(17, 22, 2),
    createVerticalPath(21, 22, 2),

    createHorizontalPath(14, 23, 2),

    createHorizontalPath(11, 17, 8),
    createHorizontalPath(11, 13, 4),
    createVerticalPath(11, 13, 5),
    createVerticalPath(18, 13, 5),
    createVerticalPath(11.5, 13.5, 4.0),
    createVerticalPath(17.5, 13.5, 4.0),
    createHorizontalPath(11.5, 13.5, 3.5),
    createHorizontalPath(15.0, 13.5, 3.5),
    createHorizontalPath(11.5, 16.5, 7.0),
    createHorizontalPath(15, 13, 4),

    createHorizontalPath(11, 19, 8),
    createHorizontalPath(11, 20, 4),
    createHorizontalPath(15, 20, 4),
    createVerticalPath(11, 19, 2),
    createVerticalPath(18, 19, 2),
    createVerticalPath(14, 20, 4),
    createVerticalPath(15, 20, 4),

    createVerticalPath(8, 17, 4),
    createVerticalPath(9, 17, 4),
    createHorizontalPath(8, 20, 2),
    createHorizontalPath(8, 17, 2),

    createHorizontalPath(8, 14, 2),
    createHorizontalPath(8, 7, 2),
    createVerticalPath(8, 7, 8),
    createVerticalPath(9, 11, 4),
    createVerticalPath(9, 7, 4),
    createHorizontalPath(9, 11, 4),
    createHorizontalPath(9, 10, 4),
    createVerticalPath(12, 10, 2),

    createHorizontalPath(20, 20, 2),
    createVerticalPath(20, 17, 4),
    createVerticalPath(21, 17, 4),
    createHorizontalPath(20, 17, 2),

    createVerticalPath(21, 7, 8),
    createVerticalPath(20, 7, 4),
    createVerticalPath(20, 11, 4),
    createVerticalPath(17, 10, 2),
    createHorizontalPath(17, 10, 4),
    createHorizontalPath(17, 11, 4),
    createHorizontalPath(20, 14, 2),
    createHorizontalPath(20, 7, 2),

    createHorizontalPath(11, 7, 8),
    createHorizontalPath(11, 8, 4),
    createHorizontalPath(15, 8, 4),
    createHorizontalPath(14, 11, 2),
    createVerticalPath(14, 8, 4),
    createVerticalPath(15, 8, 4),
    createVerticalPath(11, 7, 2),
    createVerticalPath(18, 7, 2),


    createHorizontalPath(3, 7, 4),
    createHorizontalPath(3, 8, 4),
    createVerticalPath(3, 7, 2),
    createVerticalPath(6, 7, 2),

    createHorizontalPath(23, 7, 4),
    createHorizontalPath(23, 8, 4),
    createVerticalPath(23, 7, 2),
    createVerticalPath(26, 7, 2),

    createHorizontalPath(3, 3, 4),
    createHorizontalPath(3, 5, 4),
    createVerticalPath(3, 3, 3),
    createVerticalPath(6, 3, 3),

    createHorizontalPath(8, 3, 5),
    createHorizontalPath(8, 5, 5),
    createVerticalPath(8, 3, 3),
    createVerticalPath(12, 3, 3),

    createHorizontalPath(17, 3, 5),
    createHorizontalPath(17, 5, 5),
    createVerticalPath(17, 3, 3),
    createVerticalPath(21, 3, 3),

    createHorizontalPath(23, 3, 4),
    createHorizontalPath(23, 5, 4),
    createVerticalPath(23, 3, 3),
    createVerticalPath(26, 3, 3),
)

