package com.syrous.pacman

import android.util.Log
import com.syrous.pacman.util.EnemyChaseSeconds
import com.syrous.pacman.util.FoodRadius
import com.syrous.pacman.util.Fraction_1_2
import com.syrous.pacman.util.Fraction_1_4
import com.syrous.pacman.util.Fraction_3_4
import com.syrous.pacman.util.NumberOfEnemies
import com.syrous.pacman.util.PacmanRadius
import com.syrous.pacman.util.WallHeight
import com.syrous.pacman.util.WallWidth
import com.syrous.pacman.util.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextInt

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0
    private var chaseSeconds = 0

    override val pacman =
        MutableStateFlow(Pacman(Pair(0f, 0f), Pair(0f, 0f), Directions.RIGHT, Directions.RIGHT))
    override val vWallList = MutableStateFlow<List<Pair<Float, Float>>>(listOf())
    override val hWallList = MutableStateFlow<List<Pair<Float, Float>>>(listOf())
    override val foodList = MutableStateFlow<List<Pair<Int, Int>>>(listOf())
    override val score = MutableStateFlow(0)
    override val enemies = MutableStateFlow<List<Enemy>>(listOf())

    override fun updateScreenDimensions(width: Int, height: Int) {
        if (width != screenWidth && height != screenHeight) {
            screenWidth = width
            screenHeight = height
            Log.d("PacmanState", "width -> $screenWidth, height -> $screenHeight")
            initializePacman()
            initializeWall()
            initializeEnemies()
            populateFood()
        }
    }

    override fun updatePacmanPositionAfterLoop() {
        val prevPacman = pacman.value
        pacman.value = Pacman(
            position = prevPacman.position + prevPacman.direction.move,
            previousPosition = prevPacman.position,
            direction = prevPacman.direction,
            previousDirection = prevPacman.direction
        )
    }

    override fun updateEnemyPositionAfterLoop() {
        for (enemy in enemies.value) {
            when (enemy.enemyMode) {
                EnemyModes.PATROLLING -> {
                    enemyPatrollingRoute(enemy)
                }

                EnemyModes.CHASING -> {
                    enemyChasePacman(enemy)
                }

                EnemyModes.FLEEING -> {

                }
            }
        }
    }

    private fun enemyChasePacman(enemy: Enemy) {
        //A star Algo
        val enemyList = enemies.value.toMutableList()
        enemyList.remove(enemy)
        if (chaseSeconds <= EnemyChaseSeconds) {
            var minDistance = Float.MAX_VALUE
            var preferredDirection = Directions.RIGHT
            for (dir in getAllowedDirections(enemy)) {
                val newPosition = enemy.position + dir.move
                val distance = getEuclideanDistanceBetween(newPosition, pacman.value.position)
                Log.d("PacmanStateImpl", "distance between enemy and pacman -> $distance")
                if (distance < minDistance) {
                    minDistance = distance
                    preferredDirection = dir
                }
            }
            val newMove = enemy.position + preferredDirection.move
            enemyList.add(enemy.copy(position = newMove))
            chaseSeconds += 1
        } else {
            chaseSeconds = 0
            enemyList.add(enemy.copy(enemyMode = EnemyModes.PATROLLING))
        }
        enemies.value = enemyList
    }

    private fun getAllowedDirections(enemy: Enemy): List<Directions> {
        return Directions.entries.toList().filterNot { it == getOppositeDirection(enemy.direction) }
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
        start: Pair<Float, Float>,
        target: Pair<Float, Float>
    ): Float {
        return sqrt((target.first - start.first).pow(2) + (target.second - start.second).pow(2))
    }

    private fun checkPacmanNearBy(enemy: Enemy): Boolean {
        val xRange = enemy.position.first - 15f..enemy.position.first + 15f
        val yRange = enemy.position.second - 15f..enemy.position.second + 15f
        return pacman.value.position.first in xRange && pacman.value.position.second in yRange
    }

    private fun enemyPatrollingRoute(enemy: Enemy) {
        val enemyList = enemies.value.toMutableList()
        if (checkPacmanNearBy(enemy)) {
            enemyList.remove(enemy)
            enemyList.add(enemy.copy(enemyMode = EnemyModes.CHASING))
            enemies.value = enemyList
        } else {
            val moveDirection = when (enemy.direction) {
                Directions.LEFT -> {
                    when {
                        enemy.position.first < 2f && enemy.position.second > screenHeight * Fraction_1_2 -> Directions.UP
                        enemy.position.first < 2f && enemy.position.second < screenHeight * Fraction_1_2 -> Directions.DOWN
                        else -> Directions.LEFT
                    }
                }

                Directions.RIGHT -> {
                    when {
                        enemy.position.first > screenWidth - 2f && enemy.position.second > screenHeight * Fraction_1_2 -> Directions.UP
                        enemy.position.first > screenWidth - 2f && enemy.position.second < screenHeight * Fraction_1_2 -> Directions.DOWN
                        else -> Directions.RIGHT
                    }
                }

                Directions.UP -> {
                    when {
                        enemy.position.second < 2f && enemy.position.first > screenWidth * Fraction_1_2 -> Directions.LEFT
                        enemy.position.second < 2f && enemy.position.first < screenWidth * Fraction_1_2 -> Directions.RIGHT
                        else -> Directions.UP
                    }
                }

                Directions.DOWN -> {
                    when {
                        enemy.position.second > screenHeight - 2f && enemy.position.first > screenWidth * Fraction_1_2 -> Directions.LEFT
                        enemy.position.second > screenHeight - 2f && enemy.position.first < screenWidth * Fraction_1_2 -> Directions.RIGHT
                        else -> Directions.DOWN
                    }
                }
            }
            val newMove = enemy.position + moveDirection.move
            enemyList.remove(enemy)
            enemyList.add(
                Enemy(
                    position = newMove,
                    direction = moveDirection,
                    imageId = enemy.imageId,
                    enemyMode = EnemyModes.PATROLLING
                )
            )
            enemies.value = enemyList
        }
    }

    private fun initializeEnemies() {
        enemies.value = buildList {
            repeat(NumberOfEnemies) {
                add(
                    Enemy(
                        position = if (it == 0) 4f to 4f else screenWidth - 4f * it.toFloat() to screenHeight - 4f * it.toFloat(),
                        direction = if (it == 0) Directions.DOWN else Directions.UP,
                        imageId = if (it == 0) R.drawable.ghost_red else R.drawable.ghost_orange,
                        enemyMode = EnemyModes.PATROLLING
                    )
                )
            }
        }
    }


    private fun canHaveFood(pacman: Pacman): Pair<Int, Int>? {
        for (food in foodList.value) {
            val pacmanXRange = pacman.position.first..pacman.position.first + PacmanRadius
            val pacmanYRange =
                pacman.position.second..pacman.position.second + (PacmanRadius * Fraction_1_2)
            if (food.first.toFloat() in pacmanXRange && food.second.toFloat() in pacmanYRange) {
                return food
            }
        }
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
                foodList.value = foodList.value.filterNot { it == canHaveFoodResult }
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
                foodList.value = foodList.value.filterNot { it == canHaveFoodResult }
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
                foodList.value = foodList.value.filterNot { it == canHaveFoodResult }
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
                foodList.value = foodList.value.filterNot { it == canHaveFoodResult }
                score.value += 1
                pacman.value = newMove
            }

            else -> pacman.value = newMove
        }
    }

    private fun initializePacman() {
        pacman.value = Pacman(
            position = screenWidth * Fraction_1_2 to screenHeight * Fraction_1_2,
            previousPosition = screenWidth * Fraction_1_2 to screenHeight * Fraction_1_2,
            direction = Directions.RIGHT,
            previousDirection = Directions.RIGHT
        )
    }

    private fun initializeWall() {
        hWallList.value = buildList {
            add(
                Pair(
                    screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2),
                    screenHeight * Fraction_1_4
                )
            )
            add(
                Pair(
                    screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2),
                    screenHeight * Fraction_1_4
                )
            )
            add(
                Pair(
                    screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2),
                    screenHeight * Fraction_3_4
                )
            )
            add(
                Pair(
                    screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2),
                    screenHeight * Fraction_3_4
                )
            )
        }

        vWallList.value = buildList {
            add(
                Pair(
                    hWallList.value[0].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2),
                    hWallList.value[0].second
                )
            )
            add(
                Pair(
                    hWallList.value[1].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2),
                    hWallList.value[1].second
                )
            )
            add(
                Pair(
                    hWallList.value[2].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2),
                    hWallList.value[2].second - 5.5f
                )
            )
            add(
                Pair(
                    hWallList.value[3].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2),
                    hWallList.value[3].second - 5.5f
                )
            )
        }
    }

    private fun populateFood() {
        foodList.value = buildList {
            repeat(50) {
                val food = generateFood()
                if (food != null) add(food)
            }
        }
    }

    private fun checkDrawOnWall(
        foodPoint: Pair<Int, Int>, wallList: List<Pair<Float, Float>>
    ): Boolean {
        for (wall in wallList) {
            if (foodPoint.first in wall.first.toInt() - FoodRadius.value.toInt()..WallHeight + FoodRadius.value.toInt() && foodPoint.second in wall.second.toInt() - FoodRadius.value.toInt()..WallWidth + FoodRadius.value.toInt()) return true
        }
        return false
    }

    private fun checkDrawOnBoundary(foodPoint: Pair<Int, Int>): Boolean {
        return foodPoint.first !in 1 until screenWidth - FoodRadius.value.toInt() && foodPoint.second !in 1 until screenHeight - FoodRadius.value.toInt()
    }

    private fun checkDrawOnSelf(foodPoint: Pair<Int, Int>): Boolean {
        for (food in foodList.value) {
            if (foodPoint.first in food.first..food.first + FoodRadius.value.toInt() || food.second in food.second..food.second + FoodRadius.value.toInt()) return true
        }
        return false
    }

    private fun generateFood(): Pair<Int, Int>? {
        val randomPoint = nextInt(screenWidth) to nextInt(screenHeight)
        return when {
            checkDrawOnWall(randomPoint, hWallList.value) -> null
            checkDrawOnWall(randomPoint, vWallList.value) -> null
            checkDrawOnBoundary(randomPoint) -> null
            checkDrawOnSelf(randomPoint) -> null
            else -> randomPoint
        }
    }
}

enum class Directions(val move: Pair<Float, Float>, val angle: Float) {
    LEFT(Pair(-1f, 0f), -180f), RIGHT(Pair(1f, 0f), 0f), UP(Pair(0f, -1f), -90f), DOWN(
        Pair(0f, 1f),
        90f
    ),
}

enum class EnemyModes {
    PATROLLING, CHASING, FLEEING
}