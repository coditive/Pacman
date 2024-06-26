package com.syrous.pacman.util

import com.syrous.pacman.model.Path
import com.syrous.pacman.model.WallPath.Companion.createHorizontalPath
import com.syrous.pacman.model.WallPath.Companion.createVerticalPath


const val UnitScale = 9
const val PacmanRadius = 25f
const val FoodRadius = 1
const val EnergizerRadius = 5
const val CutAngle = 40f
const val EatAngle = 60f
const val GhostSize = 80


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
    Path.createVerticalPath(10, 12, 9),
    Path.createVerticalPath(19, 12, 9),
    Path.createHorizontalPath(10, 12, 3),
    Path.createHorizontalPath(16, 12, 3),
    Path.createVerticalPath(13, 9, 4),
    Path.createVerticalPath(16, 9, 4),
    Path.createTunnelPath(0, 15, 7),
    Path.createHorizontalPath(7, 15, 4),
    Path.createHorizontalPath(19, 15, 4),
    Path.createTunnelPath(23, 15, 7),
    Path.createHorizontalPath(10, 18, 10),
    Path.createHorizontalPath(13, 12, 4),
    Path.createHorizontalPath(13, 24, 4),
)


val VERTICAL_WALL_LIST = listOf(
    createVerticalPath(0.5, 0.5, 11.0),
    createVerticalPath(1, 1, 10),
    createVerticalPath(6, 10, 5),
    createVerticalPath(5.5, 10.5, 4.0),
    createVerticalPath(5.5, 16.5, 4.0),
    createVerticalPath(6, 16, 5),
    createVerticalPath(0.5, 19.5, 13.0),
    createVerticalPath(1, 20, 6),
    createVerticalPath(3, 25, 2),
    createVerticalPath(1, 26, 6),
    createVerticalPath(14, 1, 5),
    createVerticalPath(15, 1, 5),
    createVerticalPath(28, 1, 10),
    createVerticalPath(28, 1, 10),
    createVerticalPath(28.5, 0.5, 11.0),
    createVerticalPath(23, 10, 5),
    createVerticalPath(23.5, 10.5, 4.0),
    createVerticalPath(23, 16, 5),
    createVerticalPath(23.5, 16.5, 4.0),
    createVerticalPath(28, 20, 6),
    createVerticalPath(28.5, 19.5, 13.0),
    createVerticalPath(26, 25, 2),
    createVerticalPath(28, 26, 6),
    createVerticalPath(8, 25, 4),
    createVerticalPath(9, 25, 4),
    createVerticalPath(3, 28, 2),
    createVerticalPath(12, 28, 2),
    createVerticalPath(21, 25, 4),
    createVerticalPath(20, 25, 4),
    createVerticalPath(26, 28, 2),
    createVerticalPath(17, 28, 2),
    createVerticalPath(28.5, 0.5, 11.0),
    createVerticalPath(3, 22, 2),
    createVerticalPath(6, 22, 5),
    createVerticalPath(5, 23, 4),
    createVerticalPath(23, 22, 5),
    createVerticalPath(24, 23, 4),
    createVerticalPath(26, 22, 2),
    createVerticalPath(14, 26, 4),
    createVerticalPath(15, 26, 4),
    createVerticalPath(11, 25, 2),
    createVerticalPath(18, 25, 2),
    createVerticalPath(8, 22, 2),
    createVerticalPath(12, 22, 2),
    createVerticalPath(17, 22, 2),
    createVerticalPath(21, 22, 2),
    createVerticalPath(11, 13, 5),
    createVerticalPath(18, 13, 5),
    createVerticalPath(11.5, 13.5, 4.0),
    createVerticalPath(17.5, 13.5, 4.0),
    createVerticalPath(11, 19, 2),
    createVerticalPath(18, 19, 2),
    createVerticalPath(14, 20, 4),
    createVerticalPath(15, 20, 4),
    createVerticalPath(8, 17, 4),
    createVerticalPath(9, 17, 4),
    createVerticalPath(8, 7, 8),
    createVerticalPath(9, 11, 4),
    createVerticalPath(9, 7, 4),
    createVerticalPath(12, 10, 2),
    createVerticalPath(20, 17, 4),
    createVerticalPath(21, 17, 4),
    createVerticalPath(21, 7, 8),
    createVerticalPath(20, 7, 4),
    createVerticalPath(20, 11, 4),
    createVerticalPath(17, 10, 2),
    createVerticalPath(14, 8, 4),
    createVerticalPath(15, 8, 4),
    createVerticalPath(11, 7, 2),
    createVerticalPath(18, 7, 2),
    createVerticalPath(3, 7, 2),
    createVerticalPath(6, 7, 2),
    createVerticalPath(23, 7, 2),
    createVerticalPath(26, 7, 2),
    createVerticalPath(3, 3, 3),
    createVerticalPath(6, 3, 3),
    createVerticalPath(8, 3, 3),
    createVerticalPath(12, 3, 3),
    createVerticalPath(17, 3, 3),
    createVerticalPath(21, 3, 3),
    createVerticalPath(23, 3, 3),
    createVerticalPath(26, 3, 3),
)

val HORIZONTAL_WALL_LIST = listOf(
    createHorizontalPath(1, 10, 6),
    createHorizontalPath(0.5, 10.5, 6.0),
    createHorizontalPath(0.0, 13.5, 6.5),
    createHorizontalPath(0, 14, 7),
    createHorizontalPath(0, 16, 7),
    createHorizontalPath(0.0, 16.5, 6.5),
    createHorizontalPath(0.5, 19.5, 6.0),
    createHorizontalPath(1, 20, 6),
    createHorizontalPath(1, 25, 3),
    createHorizontalPath(1, 26, 3),
    createHorizontalPath(0.5, 31.5, 29.0),
    createHorizontalPath(1, 31, 28),
    createHorizontalPath(0.5, 0.5, 29.0),
    createHorizontalPath(1, 1, 14),
    createHorizontalPath(14, 5, 2),
    createHorizontalPath(15, 1, 14),
    createHorizontalPath(23, 10, 6),
    createHorizontalPath(23.5, 10.5, 6.0),
    createHorizontalPath(23, 14, 8),
    createHorizontalPath(23.5, 13.5, 7.5),
    createHorizontalPath(23, 16, 8),
    createHorizontalPath(23.5, 16.5, 7.5),
    createHorizontalPath(23, 20, 6),
    createHorizontalPath(23.5, 19.5, 6.0),
    createHorizontalPath(26, 25, 3),
    createHorizontalPath(26, 26, 3),
    createHorizontalPath(3, 29, 10),
    createHorizontalPath(3, 28, 6),
    createHorizontalPath(9, 28, 4),
    createHorizontalPath(8, 25, 2),
    createHorizontalPath(17, 29, 10),
    createHorizontalPath(21, 28, 6),
    createHorizontalPath(17, 28, 4),
    createHorizontalPath(20, 25, 2),
    createHorizontalPath(3, 22, 4),
    createHorizontalPath(3, 23, 3),
    createHorizontalPath(5, 26, 2),
    createHorizontalPath(23, 22, 4),
    createHorizontalPath(23, 26, 2),
    createHorizontalPath(24, 23, 3),
    createHorizontalPath(11, 25, 8),
    createHorizontalPath(11, 26, 4),
    createHorizontalPath(15, 26, 4),
    createHorizontalPath(14, 29, 2),
    createHorizontalPath(8, 22, 5),
    createHorizontalPath(8, 23, 5),
    createHorizontalPath(17, 22, 5),
    createHorizontalPath(17, 23, 5),
    createHorizontalPath(14, 23, 2),
    createHorizontalPath(11, 17, 8),
    createHorizontalPath(11.0, 13.0, 3.5),
    createHorizontalPath(11.5, 13.5, 3.0),
    createHorizontalPath(15.5, 13.5, 3.0),
    createHorizontalPath(11.5, 16.5, 7.0),
    createHorizontalPath(15.5, 13.0, 3.5),
    createHorizontalPath(11, 19, 8),
    createHorizontalPath(11, 20, 4),
    createHorizontalPath(15, 20, 4),
    createHorizontalPath(8, 20, 2),
    createHorizontalPath(8, 17, 2),
    createHorizontalPath(8, 14, 2),
    createHorizontalPath(8, 7, 2),
    createHorizontalPath(9, 11, 4),
    createHorizontalPath(9, 10, 4),
    createHorizontalPath(20, 20, 2),
    createHorizontalPath(20, 17, 2),
    createHorizontalPath(17, 10, 4),
    createHorizontalPath(17, 11, 4),
    createHorizontalPath(20, 14, 2),
    createHorizontalPath(20, 7, 2),
    createHorizontalPath(11, 7, 8),
    createHorizontalPath(11, 8, 4),
    createHorizontalPath(15, 8, 4),
    createHorizontalPath(14, 11, 2),
    createHorizontalPath(3, 7, 4),
    createHorizontalPath(3, 8, 4),
    createHorizontalPath(23, 7, 4),
    createHorizontalPath(23, 8, 4),
    createHorizontalPath(3, 3, 4),
    createHorizontalPath(3, 5, 4),
    createHorizontalPath(8, 3, 5),
    createHorizontalPath(8, 5, 5),
    createHorizontalPath(17, 3, 5),
    createHorizontalPath(17, 5, 5),
    createHorizontalPath(23, 3, 4),
    createHorizontalPath(23, 5, 4),
)

val ENERGIZER_POSITION = listOf(
    Pair(2, 4),
    Pair(2, 24),
    Pair(2, 30),
    Pair(10, 9),
    Pair(19, 9),
    Pair(27, 24),
    Pair(27, 4),
    Pair(27, 30)
)

val CAGE_ENTRANCE_TILE = Pair(14f, 12f)

val TUNNEL_POSITION = listOf(
    Pair(1, 15),
    Pair(28, 15)
)


const val ghostSpeed = 0.75f
const val ghostTunnelSpeed = 0.4f
const val playerSpeed = 0.8f
const val foodEatingSpeed = 0.71f
const val ghostFrightSpeed = 0.5f
const val playerFrightSpeed = 0.9f
const val foodEatingFrightSpeed = 0.79f
const val elroyDotsLeftPart1 = 20
const val elroySpeedPart1 = 0.8f
const val elroyDotsLeftPart2 = 10
const val elroySpeedPart2 = 0.85f
const val frightTime = 6
const val frightBlinkCount = 5
const val fruit = 1
const val fruitScore = 100
val ghostModeSwitchTimes: DoubleArray = doubleArrayOf(7.0, 20.0, 7.0, 20.0, 5.0, 20.0, 5.0, 1.0)
const val cageForceTime = 4
val cageLeavingLimits: DoubleArray = doubleArrayOf(0.0, 0.0, 30.0, 60.0)

const val LEAVING_SPEED = 0.8f * 0.4f
const val IN_CAGE_SPEED = 0.8f * 0.3f