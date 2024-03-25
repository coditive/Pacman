package com.syrous.pacman

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0

    override val pacman = MutableStateFlow(Pair(0.0f, 0.0f))
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

    private fun initializePacman() {
        pacman.value = screenWidth * Fraction_1_2 to screenHeight * Fraction_1_2
    }

    private fun initializeWall() {
        hWallList.value = buildList {
            add(Pair(screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_1_4))
            add(Pair(screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_1_4))
            add(Pair(screenWidth * Fraction_1_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_3_4))
            add(Pair(screenWidth * Fraction_3_4 - (WallHeight * Fraction_1_2), screenHeight * Fraction_3_4))
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
                add(food)
            }
        }
    }

    private fun generateFood(): Pair<Int, Int> {
        return Random.nextInt(screenWidth) to Random.nextInt(screenHeight)
    }

}