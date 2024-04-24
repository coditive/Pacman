package com.syrous.pacman

import android.util.Log
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.ENERGIZER_POSITION
import com.syrous.pacman.util.EnemyChaseSeconds
import com.syrous.pacman.util.Fraction_1_2
import com.syrous.pacman.util.Fraction_1_4
import com.syrous.pacman.util.GhostSize
import com.syrous.pacman.util.HORIZONTAL_WALL_LIST
import com.syrous.pacman.util.NumberOfEnemies
import com.syrous.pacman.util.PATHS
import com.syrous.pacman.util.PATH_WITHOUT_FOOD
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.VERTICAL_WALL_LIST
import com.syrous.pacman.util.minus
import com.syrous.pacman.util.plus
import com.syrous.pacman.util.times
import com.syrous.pacman.util.toFloat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0
    private var gameWidth = 0
    private var gameHeight = 0
    private var chaseSeconds = 0
    private var scaleFactorX = 0
    private var scaleFactorY = 0
    private var requestedChangeDir: Directions = Directions.NONE
    private var totalFood = 0
    private var foodEaten = 0

    override val pacman =
        MutableStateFlow(
            Pacman(
                Pair(0f, 0f),
                Pair(0, 0),
                Pair(0, 0),
                Pair(0f, 0f),
                Directions.RIGHT,
                Directions.RIGHT,
            )
        )
    override val playField: MutableMap<Int, MutableMap<Int, Tile>> = mutableMapOf()
    override val hWallList = MutableStateFlow(hashMapOf<Pair<Float, Float>, Pair<Float, Float>>())
    override val vWallList = MutableStateFlow(hashMapOf<Pair<Float, Float>, Pair<Float, Float>>())
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
            buildPacmanWalls()
            initializePlayField()
            preparePaths()
            prepareAllowedDirections()
            createPlayFieldElements()
        }
    }

    private fun determinePlayingFieldSize() {
        for (p in HORIZONTAL_WALL_LIST + VERTICAL_WALL_LIST) {
            if (p.horizontalLength > 0) {
                val x = p.x + p.horizontalLength - 1
                if (x > gameWidth) {
                    gameWidth = x.toInt()
                }
            } else {
                val y = p.y + p.verticalLength - 1
                if (y > gameHeight) {
                    gameHeight = y.toInt()
                }
            }
        }

        scaleFactorX = (screenWidth / (gameWidth * UnitScale)) * UnitScale
        scaleFactorY = (screenHeight / (gameHeight * UnitScale)) * UnitScale
        Log.d("PacmanStateImpl", "Game Dimension -> x -> $gameWidth, y -> $gameHeight")
    }

    private fun buildPacmanWalls() {
        Log.d("PacmanStateImpl", "BuildPacmanWalls Called!!!")
        val vWallPointList = hashMapOf<Pair<Float, Float>, Pair<Float, Float>>()
        val hWallPointList = hashMapOf<Pair<Float, Float>, Pair<Float, Float>>()
        for (wall in VERTICAL_WALL_LIST + HORIZONTAL_WALL_LIST) {
            if (wall.horizontalLength > 0) {
                val x = wall.x + wall.horizontalLength - 1
                hWallPointList[Pair(
                    wall.x.toFloat() * scaleFactorX,
                    wall.y.toFloat() * scaleFactorY
                )] = Pair(x.toFloat() * scaleFactorX, wall.y.toFloat() * scaleFactorY)
            } else {
                val y = wall.y + wall.verticalLength - 1
                vWallPointList[Pair(
                    wall.x.toFloat() * scaleFactorX,
                    wall.y.toFloat() * scaleFactorY
                )] = Pair(wall.x.toFloat() * scaleFactorX, y.toFloat() * scaleFactorY)
            }
        }
        Log.d("PacmanStateImpl", "wallList will be updated")
        vWallList.value = vWallPointList
        hWallList.value = hWallPointList
        Log.d("PacmanStateImpl", "wallList is updated!!!")
    }

    private fun initializePlayField() {
        for (x in 0..gameWidth + 1) {
            val col = hashMapOf<Int, Tile>()
            for (y in 0..gameHeight + 1) {
                val tile =
                    Tile(isIntersection = false, isPath = false, isTunnel = false, food = Food.NONE)
                col[y * UnitScale] = tile
            }
            playField[x * UnitScale] = col
        }
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
        for (p in PATHS) {
            val startX = p.x * UnitScale
            val startY = p.y * UnitScale
            Log.d("PacmanStateImpl", "prepare path -> path -> $p")
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

        for (p in PATH_WITHOUT_FOOD) {
            if (p.horizontalLength != 0) {
                for (x in p.x * UnitScale..(p.x + p.horizontalLength - 1) * UnitScale step UnitScale) {
                    playField[x]!![p.y * UnitScale] =
                        playField[x]!![p.y * UnitScale]!!.copy(food = Food.NONE)
                }
            } else {
                for (y in p.y * UnitScale..(p.y + p.verticalLength - 1) * UnitScale step UnitScale) {
                    playField[p.x * UnitScale]!![y] =
                        playField[p.x * UnitScale]!![y]!!.copy(food = Food.NONE)
                }
            }
        }

    }

    private fun createPlayFieldElements() {
        createFood()
        initializePacman()
        initializeGhosts()
    }

    private fun createFood() {
        val food = foodList.value
        val scaleFactorX = (screenWidth / (gameWidth * UnitScale))
        val scaleFactorY = (screenHeight / (gameHeight * UnitScale))
        Log.d("PacmanStateImpl", "scaleFactorX -> $scaleFactorX, scaleFactorY -> $scaleFactorY")
        for (x in UnitScale..gameWidth * UnitScale step UnitScale) {
            val col = mutableMapOf<Int, Food>()
            for (y in UnitScale..gameHeight * UnitScale step UnitScale) {
                if (playField[x]!![y]!!.food != Food.NONE) {
                    col[y * scaleFactorY] = Food.PELLET
                }
                if (Pair(x / UnitScale, y / UnitScale) in ENERGIZER_POSITION) {
                    col[y * scaleFactorY] = Food.ENERGIZER
                }
                totalFood += 1
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
                if (playField[x]!![y - UnitScale]!!.isPath) {
                    allowedDir.add(Directions.UP)
                }
                if (playField[x]!![y + UnitScale]!!.isPath) {
                    allowedDir.add(Directions.DOWN)
                }
                if (playField[x - UnitScale]!![y]!!.isPath) {
                    allowedDir.add(Directions.LEFT)
                }
                if (playField[x + UnitScale]!![y]!!.isPath) {
                    allowedDir.add(Directions.RIGHT)
                }
                playField[x]!![y] = playField[x]!![y]!!.copy(allowedDir = allowedDir)
            }
        }
    }

    override suspend fun updatePositionAfterLoop() {
        if (requestedChangeDir != Directions.NONE) {
            handleDirectionChange(requestedChangeDir)
            requestedChangeDir = Directions.NONE
        }
        updatePacmanPositionAfterLoop()
        updateEnemyPositionAfterLoop()
    }

    private fun updatePacmanPositionAfterLoop() {
        if (scaleFactorX != 0 && scaleFactorY != 0) {
            val pos = pacman.value.position
            val dir = pacman.value.direction
            val tilePos = pacman.value.tilePos

            val pacX = pos.first + dir.move.first
            val pacY = pos.second + dir.move.second
            val newPos = Pair(pacX, pacY)

            pacman.value = pacman.value.copy(
                position = newPos,
                screenPos = Pair(
                    newPos.first.div(UnitScale) * scaleFactorX,
                    newPos.second.div(UnitScale) * scaleFactorY
                )
            )

            val imaginaryX = pacX / UnitScale
            val imaginaryY = pacY / UnitScale
            val nextTile = Pair(
                round(imaginaryX).toInt(),
                round(imaginaryY).toInt()
            )
            val enteredTile = Pair(
                floor(imaginaryX) * UnitScale,
                floor(imaginaryY) * UnitScale
            )
            Log.d(
                "PacmanStateImpl",
                "step fun -> After  -> pos -> $pos, newPos -> $newPos, imaginary => $imaginaryX, $imaginaryY, newTile -> $nextTile, tilePos => $tilePos, enteredTile => $enteredTile"
            )

            when {
                nextTile.first != tilePos.first || nextTile.second != tilePos.second -> {
                    pacmanEnteringTile(nextTile)
                }

                enteredTile == newPos -> {
                    pacmanEnteredTile()
                }
            }
        }
    }

    private fun handleDirectionChange(inputDir: Directions) {
        var dir = pacman.value.direction
        val tilePos = pacman.value.tilePos
        Log.d(
            "PacmanStateImpl",
            "handleDirectionChange -> inputDir -> $inputDir, pacman dir -> $dir "
        )
        if (dir == getOppositeDirection(inputDir)) {
            dir = inputDir
            Log.d(
                "PacmanStateImpl",
                "handleDirectionChange in opposite dir -> inputDir -> $inputDir "
            )
            pacman.value = pacman.value.copy(direction = dir, nextDir = Directions.NONE)
        } else if (dir != inputDir) {
            val tile = getPlayFieldTile(tilePos)
            val playFieldTile = playField[tile.first]!![tile.second]!!
            if (dir == Directions.NONE) {
                Log.d(
                    "PacmanStateImpl",
                    "handleDirectionChange in opposite dir -> inputDir -> $inputDir, pacman dir -> $dir, isIntersection -> ${playFieldTile.isIntersection}, allowedDir -> ${playFieldTile.allowedDir}"
                )
                if (playFieldTile.allowedDir.contains(inputDir)) {
                    dir = inputDir
                    Log.d(
                        "PacmanStateImpl",
                        "handleDirectionChange in contains dir -> inputDir -> $inputDir, pacman dir -> $dir "
                    )
                    pacman.value = pacman.value.copy(direction = dir)
                }
            } else {
                if (playFieldTile.allowedDir.contains(inputDir)) {
                    var pastPos = pacman.value.position
                    val pastTilePos = pacman.value.tilePos
                    pastPos -= pastPos + inputDir.move
                    var stepPassed = 0
                    if (pastPos.first.toInt() == pastTilePos.first && pastPos.second.toInt() == pastTilePos.second) {
                        stepPassed = 1
                    } else {
                        pastPos -= pastPos + inputDir.move
                        if (pastPos.first.toInt() == pastTilePos.first && pastPos.second.toInt() == pastTilePos.second) {
                            stepPassed = 2
                        }
                    }

                    if (stepPassed != 0) {
                        // the input of direction is slightly delayed,
                        // correct the location according to the new direction.
                        dir = inputDir
                        val pastTile = pacman.value.tilePos
                        val newPos = pastTile.toFloat() + dir.move * stepPassed
                        Log.d("PacmanStateImpl", "inputDir -> $inputDir ")
                        pacman.value = pacman.value.copy(
                            position = Pair(
                                newPos.first * UnitScale,
                                newPos.second * UnitScale
                            )
                        )
                    }
                }
                pacman.value = pacman.value.copy(nextDir = inputDir)
            }
        }
    }


    private fun pacmanEnteringTile(tile: Pair<Int, Int>) {
        adjustOverShootOnEnteringTile(getPlayFieldTile(tile))
        if (canHaveFood(getPlayFieldTile(tile))) {
            haveFood(getPlayFieldTile(tile))
        }
        Log.d("PacmanStateImpl", "pacmanEnteringTile pacman -> ${pacman.value} ")
        pacman.value = pacman.value.copy(tilePos = tile, lastGoodTilePos = tile)
    }

    private fun getPlayFieldTile(tile: Pair<Int, Int>): Pair<Int, Int> =
        Pair(tile.first * UnitScale, tile.second * UnitScale)

    private fun canHaveFood(playFieldTile: Pair<Int, Int>): Boolean =
        playField[playFieldTile.first]!![playFieldTile.second]!!.food != Food.NONE

    private fun haveFood(playFieldTile: Pair<Int, Int>) {
        val adjustedScaleFactorX = scaleFactorX / UnitScale
        val adjustedScaleFactorY = scaleFactorY / UnitScale
        val listOfAvailableFood = foodList.value.toMutableMap()
        playField[playFieldTile.first]!![playFieldTile.second] =
            playField[playFieldTile.first]!![playFieldTile.second]!!.copy(food = Food.NONE)
        Log.d(
            "PacmanStateImpl",
            "tile -> ${playFieldTile.first * adjustedScaleFactorX}, ${playFieldTile.second * adjustedScaleFactorY}  listOfAvailableFood -> ${listOfAvailableFood[playFieldTile.first * adjustedScaleFactorX]!![playFieldTile.second * adjustedScaleFactorY]}"
        )
        listOfAvailableFood[playFieldTile.first * adjustedScaleFactorX]!!.remove(playFieldTile.second * adjustedScaleFactorY)
        Log.d(
            "PacmanStateImpl",
            "after remove tile -> ${playFieldTile.first * adjustedScaleFactorX}, ${playFieldTile.second * adjustedScaleFactorY}  listOfAvailableFood -> ${listOfAvailableFood[playFieldTile.first * adjustedScaleFactorX]!![playFieldTile.second * adjustedScaleFactorY]}"
        )
        updateScore(playField[playFieldTile.first]!![playFieldTile.second]!!.food)
        foodList.value = listOfAvailableFood
    }

    private fun updateScore(food: Food) {
        foodEaten += 1
        score.value = foodEaten
    }

    private fun adjustOverShootOnEnteringTile(playFieldTile: Pair<Int, Int>) {
        if (playField[playFieldTile.first]!![playFieldTile.second]!!.isPath.not()) {
            val lastGoodTile = pacman.value.lastGoodTilePos
            pacman.value = pacman.value.copy(
                position = Pair(
                    lastGoodTile.first * UnitScale.toFloat(),
                    lastGoodTile.second * UnitScale.toFloat()
                ),
                tilePos = lastGoodTile,
                direction = Directions.NONE
            )
        }
    }

    private fun pacmanEnteredTile() {
        val nextDir = pacman.value.nextDir
        val tilePos = pacman.value.tilePos
        val tile = getPlayFieldTile(tilePos)
        val playFieldTile = playField[tile.first]!![tile.second]
        if (playFieldTile != null && playFieldTile.isIntersection) {
            if (playFieldTile.allowedDir.contains(nextDir)) {
                pacman.value = pacman.value.copy(direction = nextDir, nextDir = Directions.NONE)
            } else if (playFieldTile.allowedDir.contains(nextDir).not()) {
                pacman.value =
                    pacman.value.copy(direction = Directions.NONE, nextDir = Directions.NONE)
            }
        }
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
            Directions.NONE -> Directions.NONE
        }
    }

    private fun getEuclideanDistanceBetween(
        start: Pair<Float, Float>, target: Pair<Float, Float>
    ): Float = sqrt((target.first - start.first).pow(2) + (target.second - start.second).pow(2))

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

                Directions.NONE -> TODO()
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

    override fun moveUp() {
        requestedChangeDir = Directions.UP
    }

    override fun moveDown() {
        requestedChangeDir = Directions.DOWN
    }

    override fun moveLeft() {
        requestedChangeDir = Directions.LEFT
    }

    override fun moveRight() {
        requestedChangeDir = Directions.RIGHT
    }

    private fun initializePacman() {
        pacman.value = Pacman(
            position = Pair(14f * UnitScale, 24f * UnitScale),
            tilePos = Pair(14, 24),
            screenPos = Pair(14f * scaleFactorX, 24f * scaleFactorY),
//            currentSpeed = CurrentSpeed.NORMAL,
            lastGoodTilePos = Pair(14, 24),
            direction = Directions.RIGHT,
//            fullSpeed = 0.8f,
//            dotEatingSpeed = 0.71f,
//            tunnelSpeed = 0.8f,,
            nextDir = Directions.RIGHT
        )
        Log.d("PacmanStateImpl", "pacman -> ${pacman.value}")
    }
}


enum class EnemyModes {
    PATROLLING, CHASING, FLEEING
}

sealed class GameEvent {
    data object GhostAtePacman : GameEvent()
}