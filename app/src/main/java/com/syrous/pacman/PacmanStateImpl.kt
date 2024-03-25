package com.syrous.pacman

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class PacmanStateImpl : PacmanState {

    private var screenWidth = 0
    private var screenHeight = 0

    override val pacman = MutableStateFlow(Pair(0, 0))
    override val wallList = MutableStateFlow<List<Pair<Float, Float>>>(listOf())
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
        pacman.value = screenWidth / 2 to screenHeight / 2
    }

    private fun initializeWall() {
        wallList.value = buildList {
            add(Pair(screenWidth * Screen_1_4, screenHeight * Screen_1_4))
            add(Pair(screenWidth * Screen_3_4, screenHeight * Screen_1_4))
            add(Pair(screenWidth * Screen_1_4, screenHeight * Screen_3_4))
            add(Pair(screenWidth * Screen_3_4, screenHeight * Screen_3_4))
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