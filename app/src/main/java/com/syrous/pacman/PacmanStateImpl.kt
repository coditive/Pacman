package com.syrous.pacman

import android.util.Log
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.EnemyChaseSeconds
import com.syrous.pacman.util.FoodRadius
import com.syrous.pacman.util.Fraction_1_2
import com.syrous.pacman.util.Fraction_1_4
import com.syrous.pacman.util.GhostSize
import com.syrous.pacman.util.NumberOfEnemies
import com.syrous.pacman.util.PATHS_WITH_FOOD
import com.syrous.pacman.util.PacmanRadius
import com.syrous.pacman.util.PacmanUnitRadius
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.WallHeight
import com.syrous.pacman.util.WallWidth
import com.syrous.pacman.util.plus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.pow
import kotlin.math.sqrt

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0
    private var gameWidth = 0
    private var gameHeight = 0
    private var chaseSeconds = 0

    override val pacman =
        MutableStateFlow(Pacman(Pair(0f, 0f), Pair(0f, 0f), Directions.RIGHT, Directions.RIGHT))
    override val playField: MutableMap<Int, MutableMap<Int, Tile>> = mutableMapOf()
    override val foodList = MutableStateFlow<MutableMap<Int, MutableMap<Int, Food>>>(mutableMapOf())
    override val score = MutableStateFlow(0)
    override val ghosts = MutableStateFlow<List<Ghost>>(listOf())
    override val isPaused = MutableStateFlow(false)
    override val gameEvent = MutableSharedFlow<GameEvent>()

    override fun updateScreenDimensions(width: Int, height: Int) {
        if (width != screenWidth && height != screenHeight) {
            screenWidth = width
            screenHeight = height
            determinePlayingFieldSize()
            initializePlayField()
            preparePaths()
            prepareAllowedDirections()
            createPlayFieldElements()
        }
    }

    private fun determinePlayingFieldSize() {
        for (p in PATHS_WITH_FOOD) {
            if (p.horizontalLength > 0) {
                val x = p.x + p.horizontalLength - 1
                if (x > gameWidth) {
                    gameWidth = x
                }
            } else {
                val y = p.y + p.verticalLength - 1
                if (y > gameHeight) {
                    gameHeight = y
                }
            }
        }
        Log.d("PacmanStateImpl", "Game Dimension -> x -> $gameWidth, y -> $gameHeight")
    }

    private fun initializePlayField() {
        for (x in 0..gameWidth + 1) {
            val col = hashMapOf<Int, Tile>()
            for (y in 0..gameHeight + 1) {
                val tile = Tile(isIntersection = false, isPath = false, isTunnel = false, food = Food.NONE)
                col[y * UnitScale] = tile
            }
            playField[x * UnitScale] = col
        }
        Log.d("PacmanStateImpl", "playField -> x -> ${playField.entries}, y -> ${playField[0]!!.entries} ")
    }

    private fun prepareTile(x: Int, y: Int, tunnel: Boolean, food: Food): Tile {
        return Tile(
            isPath = true,
            isTunnel = tunnel,
            isIntersection = false,
            food = if (playField[x]!![y]!!.food == Food.NONE) Food.PELLET else playField[x]!![y]!!.food
        )
    }

    private fun preparePaths() {
        for (p in PATHS_WITH_FOOD) {
            val startX = p.x * UnitScale
            val startY = p.y * UnitScale
            Log.d("PacmanStateImpl", "prepare path -> path -> $p ")
            if (p.horizontalLength > 0) {
                val endX = (p.x + p.horizontalLength - 1) * UnitScale
                val y = p.y * UnitScale

                for (x in (p.x + 1) * UnitScale until endX step UnitScale) {
                    playField[x]!![y] = prepareTile(x, y, p.tunnel, playField[x]!![y]!!.food)
                }

                playField[startX]!![y] =
                    prepareTile(startX, y, p.tunnel, playField[startX]!![y]!!.food)
                playField[endX]!![y] = prepareTile(endX, y, p.tunnel, playField[endX]!![y]!!.food)
            } else {
                val endY = (p.y + p.verticalLength - 1) * UnitScale
                val x = p.x * UnitScale

                for (y in (p.y + 1) * UnitScale..endY step UnitScale) {
                    playField[x]!![y] = prepareTile(x, y, p.tunnel, playField[x]!![y]!!.food)
                }

                playField[x]!![startY] =
                    prepareTile(x, startY, p.tunnel, playField[x]!![startY]!!.food)
                playField[x]!![endY] = prepareTile(x, endY, p.tunnel, playField[x]!![endY]!!.food)
            }
        }


//        for (p in PATH_WITHOUT_FOOD) {
//            if (p.horizontalLength != 0) {
//                for(x in p.x * UnitScale..(p.x + p.horizontalLength - 1) * UnitScale step UnitScale) {
//
//                }
//            } else {
//                for(y in p.y * UnitScale..(p.y + p.verticalLength - 1) * UnitScale step UnitScale) {
//
//                }
//            }
//        }

    }

    private fun createPlayFieldElements() {
        createPellets()
        initializePacman()
        initializeWall()
        initializeGhosts()
    }

    private fun createPellets() {
        val food = foodList.value
        val scaleFactorX = (screenWidth / (gameWidth * UnitScale))
        val scaleFactorY = (screenHeight / (gameHeight * UnitScale))
        Log.d("PacmanStateImpl", "scaleFactorX -> $scaleFactorX, scaleFactorY -> $scaleFactorY")
        for(x in UnitScale..gameWidth * UnitScale step UnitScale) {
            val col = mutableMapOf<Int, Food>()
            for(y in UnitScale..gameHeight * UnitScale step UnitScale) {
                if(playField[x]!![y]!!.food != Food.NONE) {
                    col[y * scaleFactorY] = Food.PELLET
                }
            }
            food[x * scaleFactorX] = col
        }
        Log.d("PacmanStateImpl", "foodList -> $food")
        foodList.value = food
    }


    private fun prepareAllowedDirections() {
        for (x in UnitScale..gameWidth * UnitScale step UnitScale) {
            for (y in UnitScale..gameHeight * UnitScale step UnitScale) {
                val allowedDir = mutableSetOf<Directions>()
                when {
                    playField[x]!![y - UnitScale]!!.isPath -> {
                        allowedDir.add(Directions.UP)
                    }

                    playField[x]!![y + UnitScale]!!.isPath -> {
                        allowedDir.add(Directions.DOWN)
                    }

                    playField[x - UnitScale]!![y]!!.isPath -> {
                        allowedDir.add(Directions.LEFT)
                    }

                    playField[x + UnitScale]!![y]!!.isPath -> {
                        allowedDir.add(Directions.RIGHT)
                    }
                }
                playField[x]!![y] = playField[x]!![y]!!.copy(allowedDir = allowedDir)
            }
        }
    }

    override suspend fun updatePositionAfterLoop() {
        updatePacmanPositionAfterLoop()
        updateEnemyPositionAfterLoop()
    }

    private suspend fun updatePacmanPositionAfterLoop() {
        val canHaveFood = canHaveFood(pacman.value)
        val prevPacman = pacman.value
        val prevDir = pacman.value.direction
        val newMove = when {
            checkPacmanAtBoundary() -> {
                val (pacX, pacY) = prevPacman.position
                when (prevDir) {
                    Directions.LEFT -> prevPacman.copy(
                        position = Pair(
                            screenWidth - PacmanUnitRadius, pacY
                        ),
                    )

                    Directions.RIGHT -> prevPacman.copy(position = Pair(PacmanUnitRadius, pacY))

                    Directions.UP -> prevPacman.copy(
                        position = Pair(
                            pacX, screenHeight - PacmanUnitRadius
                        ),
                    )

                    Directions.DOWN -> prevPacman.copy(position = Pair(pacX, PacmanUnitRadius))
                }
            }

            canHaveFood != null -> {
                score.value += 1
                Pacman(
                    position = prevPacman.position + prevDir.move,
                    previousPosition = prevPacman.position,
                    direction = prevDir,
                    previousDirection = prevDir
                )
            }

            else -> {
                Pacman(
                    position = prevPacman.position + prevDir.move,
                    previousPosition = prevPacman.position,
                    direction = prevDir,
                    previousDirection = prevDir
                )
            }
        }
        pacman.value = newMove
    }

    private suspend fun updateEnemyPositionAfterLoop() {
        for (enemy in ghosts.value) {
            when (enemy.enemyMode) {
                EnemyModes.PATROLLING -> enemyPatrollingRoute(enemy)

                EnemyModes.CHASING -> ghostChasePacman(enemy)

                EnemyModes.FLEEING -> {

                }
            }
        }
    }

    override fun pauseGame() {
        isPaused.value = true
    }

    override fun resumeGame() {
        isPaused.value = false
    }

    private fun ghostChasePacman(ghost: Ghost) {
        //A star Algo
        val enemyList = ghosts.value.toMutableList()
        enemyList.remove(ghost)
        if (chaseSeconds <= EnemyChaseSeconds) {
            var minDistance = Float.MAX_VALUE
            var preferredDirection = Directions.RIGHT
            for (dir in getAllowedDirections(ghost)) {
                val newPosition = ghost.position + dir.move
                val distance = getEuclideanDistanceBetween(newPosition, pacman.value.position)
                if (distance < minDistance) {
                    minDistance = distance
                    preferredDirection = dir
                }
            }
            val newMove = ghost.position + preferredDirection.move
            if (minDistance in 1.5f..5.5f) {
//                gameEvent.emit(GameEvent.GhostAtePacman)
            } else {
                enemyList.add(ghost.copy(position = newMove))
                chaseSeconds += 1
            }
        } else {
            chaseSeconds = 0
            enemyList.add(ghost.copy(enemyMode = EnemyModes.PATROLLING))
        }
        ghosts.value = enemyList
    }

    private fun getAllowedDirections(ghost: Ghost): List<Directions> {
        return Directions.entries.toList().filterNot { it == getOppositeDirection(ghost.direction) }
    }

    private fun getOppositeDirection(directions: Directions): Directions {
        return when (directions) {
            Directions.LEFT -> Directions.RIGHT
            Directions.RIGHT -> Directions.LEFT
            Directions.UP -> Directions.DOWN
            Directions.DOWN -> Directions.UP
        }
    }

    private fun getEuclideanDistanceBetween(
        start: Pair<Float, Float>, target: Pair<Float, Float>
    ): Float = sqrt((target.first - start.first).pow(2) + (target.second - start.second).pow(2))

    private fun checkPacmanAtBoundary(): Boolean =
        pacman.value.position.first.toInt() !in PacmanUnitRadius.toInt()..screenWidth - PacmanUnitRadius.toInt() || pacman.value.position.second.toInt() !in PacmanUnitRadius.toInt()..screenHeight - PacmanUnitRadius.toInt()

    private fun checkPacmanNearBy(ghost: Ghost): Boolean {
        val xRange = ghost.position.first - 15f..ghost.position.first + 15f
        val yRange = ghost.position.second - 15f..ghost.position.second + 15f
        return pacman.value.position.first in xRange && pacman.value.position.second in yRange
    }

    private fun enemyPatrollingRoute(ghost: Ghost) {
        val enemyList = ghosts.value.toMutableList()
        if (checkPacmanNearBy(ghost)) {
            enemyList.remove(ghost)
            enemyList.add(ghost.copy(enemyMode = EnemyModes.CHASING))
            ghosts.value = enemyList
        } else {
            val moveDirection = when (ghost.direction) {
                Directions.LEFT -> {
                    when {
                        ghost.position.first < GhostSize && ghost.position.second > screenHeight * Fraction_1_2 -> Directions.UP
                        ghost.position.first < GhostSize && ghost.position.second < screenHeight * Fraction_1_2 -> Directions.DOWN
                        else -> Directions.LEFT
                    }
                }

                Directions.RIGHT -> {
                    when {
                        ghost.position.first > screenWidth - GhostSize && ghost.position.second > screenHeight * Fraction_1_2 -> Directions.UP
                        ghost.position.first > screenWidth - GhostSize && ghost.position.second < screenHeight * Fraction_1_2 -> Directions.DOWN
                        else -> Directions.RIGHT
                    }
                }

                Directions.UP -> {
                    when {
                        ghost.position.second < GhostSize && ghost.position.first > screenWidth * Fraction_1_2 -> Directions.LEFT
                        ghost.position.second < GhostSize && ghost.position.first < screenWidth * Fraction_1_2 -> Directions.RIGHT
                        else -> Directions.UP
                    }
                }

                Directions.DOWN -> {
                    when {
                        ghost.position.second > screenHeight - GhostSize && ghost.position.first > screenWidth * Fraction_1_2 -> Directions.LEFT
                        ghost.position.second > screenHeight - GhostSize && ghost.position.first < screenWidth * Fraction_1_2 -> Directions.RIGHT
                        else -> Directions.DOWN
                    }
                }
            }
            val newMove = ghost.position + moveDirection.move
            enemyList.remove(ghost)
            enemyList.add(
                Ghost(
                    id = ghost.id,
                    position = newMove,
                    direction = moveDirection,
                    imageId = ghost.imageId,
                    enemyMode = EnemyModes.PATROLLING
                )
            )
            ghosts.value = enemyList
        }
    }

    private fun initializeGhosts() {
        ghosts.value = buildList {
            repeat(NumberOfEnemies) {
                add(
                    Ghost(
                        id = it,
                        position = if (it == 0) GhostSize * Fraction_1_4 to GhostSize * Fraction_1_4 else screenWidth - GhostSize * it.toFloat() to screenHeight - GhostSize * it.toFloat(),
                        direction = if (it == 0) Directions.DOWN else Directions.UP,
                        imageId = if (it == 0) R.drawable.ghost_red else R.drawable.ghost_orange,
                        enemyMode = EnemyModes.PATROLLING
                    )
                )
            }
        }
    }

    private fun canHaveFood(pacman: Pacman): Pair<Int, Int>? {
        return null
    }

    override fun moveUp() {
        val canHaveFoodResult = canHaveFood(pacman.value)
        val prevPos = pacman.value.position
        val prevDir = pacman.value.direction
        val newMove = Pacman(
            position = pacman.value.position + Directions.UP.move,
            previousPosition = prevPos,
            direction = Directions.UP,
            previousDirection = prevDir
        )
        when {
            canHaveFoodResult != null -> {
                score.value += 1
                pacman.value = newMove
            }

            else -> pacman.value = newMove
        }
    }

    override fun moveDown() {
        val canHaveFoodResult = canHaveFood(pacman.value)
        val prevPos = pacman.value.position
        val prevDir = pacman.value.direction
        val newMove = Pacman(
            position = pacman.value.position + Directions.DOWN.move,
            previousPosition = prevPos,
            direction = Directions.DOWN,
            previousDirection = prevDir
        )
        when {
            canHaveFoodResult != null -> {
                score.value += 1
                pacman.value = newMove
            }

            else -> pacman.value = newMove
        }
    }

    override fun moveLeft() {
        val canHaveFoodResult = canHaveFood(pacman.value)
        val prevPos = pacman.value.position
        val prevDir = pacman.value.direction
        val newMove = Pacman(
            position = pacman.value.position + Directions.LEFT.move,
            previousPosition = prevPos,
            direction = Directions.LEFT,
            previousDirection = prevDir
        )
        when {
            canHaveFoodResult != null -> {
                score.value += 1
                pacman.value = newMove
            }

            else -> pacman.value = newMove
        }
    }

    override fun moveRight() {
        val canHaveFoodResult = canHaveFood(pacman.value)
        val prevPos = pacman.value.position
        val prevDir = pacman.value.direction
        val newMove = Pacman(
            position = pacman.value.position + Directions.RIGHT.move,
            previousPosition = prevPos,
            direction = Directions.RIGHT,
            previousDirection = prevDir
        )
        when {
            canHaveFoodResult != null -> {
                score.value += 1
                pacman.value = newMove
            }

            else -> pacman.value = newMove
        }
    }

    private fun initializePacman() {
        pacman.value = Pacman(
            position = screenWidth * Fraction_1_2 + PacmanRadius to screenHeight * Fraction_1_2 + PacmanRadius,
            previousPosition = screenWidth * Fraction_1_2 + PacmanRadius to screenHeight * Fraction_1_2 + PacmanRadius,
            direction = Directions.RIGHT,
            previousDirection = Directions.RIGHT
        )
    }

    private fun initializeWall() {

    }


    private fun checkWall(
        point: Pair<Int, Int>, wallList: List<Pair<Float, Float>>
    ): Boolean {
        for (wall in wallList) {
            if (point.first in wall.first.toInt() - FoodRadius.toInt()..WallHeight + FoodRadius.toInt() && point.second in wall.second.toInt() - FoodRadius.toInt()..WallWidth + FoodRadius.toInt()) return true
        }
        return false
    }
}


enum class EnemyModes {
    PATROLLING, CHASING, FLEEING
}

sealed class GameEvent {
    data object GhostAtePacman : GameEvent()
}