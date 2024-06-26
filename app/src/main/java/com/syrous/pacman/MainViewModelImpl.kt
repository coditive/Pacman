package com.syrous.pacman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.pacman.controller.GameController
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.floor
import kotlin.math.round

class MainViewModelImpl : ViewModel(), GameViewModel, GameController {

    override val currentScreen: MutableStateFlow<GameScreen> =
        MutableStateFlow(GameScreen.START_SCREEN)

    override var gameState: GameState = GameStateImpl()
    private var isPaused: Boolean = true
    private var isStarted: Boolean = false
    private var gameLoop: Job? = null

    //private
    private var lastTime: Long = 0L
    private val availableFps = intArrayOf(90, 45, 30)
    private var chosenFps = 2
    private var pausedTime: Long = 0L
    private var tickInterval = 0.0
    private var timeDelta = 0.0
    private var intervalTime = 0
    private var canDecreaseFps: Boolean = false
    private var tickMultiplier = 0
    private val DEFAULT_FPS = availableFps[0]
    private var slownessCount = 0


    override fun startGame() {
        currentScreen.value = GameScreen.GAME_PLAY
        isStarted = true
        gameState.updateDefaultFps(DEFAULT_FPS)
        gameState.startGamePlay()
        initializeTickInterval()
        resumeGame()
    }

    override fun pauseGame() {
        gameLoop?.cancel()
        gameState.pauseGame()
        gameLoop = null
        isPaused = true
    }

    override fun resumeGame() {
        if (isStarted && isPaused) {
            lastTime += Date().time - pausedTime
            gameState.resumeGame()
            isPaused = false
            gameLoop = viewModelScope.launch {
                while (true) {
                    gameLoop()
                    delay(round(tickInterval).toLong())
                }
            }
        }
    }

    private fun initializeTickInterval() {
        val fps = availableFps[chosenFps]
        tickInterval = (1000 / fps).toDouble()
        tickMultiplier = DEFAULT_FPS / fps
        lastTime = Date().time
        timeDelta = 0.0
        slownessCount = 0
    }

    private suspend fun gameLoop() {
        val now = Date().time
        if (isPaused) {
            pausedTime = now
            return
        }
        timeDelta += now - lastTime - tickInterval
        if (timeDelta > 100) {
            timeDelta = 100.0
        }
        if (canDecreaseFps && timeDelta > 50) {
            // If the fps can be reduced, count the number of instances where latency is over 50 ms.
            slownessCount += 1
            if (slownessCount == 20) {
                decreaseFps()
            }
        }
        var latency = 0
        if (timeDelta > tickInterval) {
            latency = floor(timeDelta / tickInterval).toInt()
            timeDelta -= (tickInterval * latency).toLong()
        }
        lastTime = now

        for (i in 0 until tickMultiplier + latency) {
            if(gameState.getGamePlayMode() == GamePlayMode.ORDINARY_PLAYING) {
                gameState.updatePositionAfterLoop()

                if(gameState.getGamePlayMode() == GamePlayMode.ORDINARY_PLAYING) {
                    gameState.updateTargetPosAfterLoop()
                }

            }
            intervalTime = (intervalTime + 1) % DEFAULT_FPS
            gameState.updateIntervalTime(intervalTime)
            gameState.handleTimers()
        }
    }

    private fun decreaseFps() {
        if (chosenFps < availableFps.size - 1) {
            chosenFps++
            initializeTickInterval()
            if (chosenFps == availableFps.size - 1) {
                canDecreaseFps = false
            }
        }
    }

    override fun endGame() {
        pauseGame()
        currentScreen.value = GameScreen.GAME_OVER
    }

    override fun moveUp() {
        if (!isPaused) {
            gameState.moveUp()
        }
    }

    override fun moveLeft() {
        if (!isPaused) {
            gameState.moveLeft()
        }
    }

    override fun moveRight() {
        if (!isPaused) {
            gameState.moveRight()
        }
    }

    override fun moveDown() {
        if (!isPaused) {
            gameState.moveDown()
        }
    }

}