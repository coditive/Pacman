package com.syrous.pacman

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random.Default.nextInt

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0

    override val pacman = MutableStateFlow(Pacman(Pair(0f, 0f), Directions.RIGHT))
    override val vWallList = MutableStateFlow<List<Pair<Float, Float>>>(listOf())
    override val hWallList = MutableStateFlow<List<Pair<Float, Float>>>(listOf())
    override val foodList = MutableStateFlow<List<Pair<Int, Int>>>(listOf())

    override fun updateScreenDimensions(width: Int, height: Int) {
        if (width != screenWidth && height != screenHeight) {
            screenWidth = width
            screenHeight = height
            Log.d("PacmanState", "width -> $screenWidth, height -> $screenHeight")
            initializePacman()
            initializeWall()
            populateFood()
        }
    }

    override fun moveUp() {
        val newMove = pacman.value.position + Directions.UP.move
        pacman.value = Pacman(newMove, Directions.UP)
    }

    override fun moveDown() {
        val newMove = pacman.value.position + Directions.DOWN.move
        pacman.value = Pacman(newMove, Directions.DOWN)    }

    override fun moveLeft() {
        val newMove = pacman.value.position + Directions.LEFT.move
        pacman.value = Pacman(newMove, Directions.LEFT)    }

    override fun moveRight() {
        val newMove = pacman.value.position + Directions.RIGHT.move
        pacman.value = Pacman(newMove, Directions.RIGHT)    }

    private fun initializePacman() {
        pacman.value = Pacman(position = screenWidth * Fraction_1_2 to screenHeight * Fraction_1_2,
            direction = Directions.RIGHT)
    }

    private fun initializeWall() {
        hWallList.value = buildList {
            add(Pair(screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_1_4))
            add(Pair(screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_1_4))
            add(Pair(screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_3_4))
            add(Pair(screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_3_4))
        }

        vWallList.value = buildList {
            add(Pair(hWallList.value[0].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2), hWallList.value[0].second))
            add(Pair(hWallList.value[1].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2), hWallList.value[1].second))
            add(Pair(hWallList.value[2].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2), hWallList.value[2].second - 5.5f))
            add(Pair(hWallList.value[3].first + (WallHeight * Fraction_1_2 - WallWidth * Fraction_1_2), hWallList.value[3].second - 5.5f))
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
        foodPoint: Pair<Int, Int>,
        wallList: List<Pair<Float, Float>>
    ): Boolean {
        for (wall in wallList) {
            if (foodPoint.first in wall.first.toInt() - FoodRadius.value.toInt() ..WallHeight + FoodRadius.value.toInt()
                && foodPoint.second in wall.second.toInt() - FoodRadius.value.toInt()..WallWidth + FoodRadius.value.toInt()
            ) return true
        }
        return false
    }

    private fun checkDrawOnBoundary(foodPoint: Pair<Int, Int>): Boolean {
        return foodPoint.first !in 1 until screenWidth - FoodRadius.value.toInt()
                && foodPoint.second !in 1 until screenHeight - FoodRadius.value.toInt()
    }

    private fun checkDrawOnSelf(foodPoint: Pair<Int, Int>): Boolean {
        for (food in foodList.value) {
            if (foodPoint.first in food.first..food.first + FoodRadius.value.toInt()
                || food.second in food.second..food.second + FoodRadius.value.toInt()
            )
                return true
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

enum class Directions(val move: Pair<Float, Float>) {
    LEFT(Pair(-1f, 0f)),
    RIGHT(Pair(1f, 0f)),
    UP(Pair(0f, -1f)),
    DOWN(Pair(0f, 1f)),
}