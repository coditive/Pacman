package com.syrous.pacman

import com.syrous.pacman.controller.ghost.BlinkyController
import com.syrous.pacman.controller.ghost.ClydeController
import com.syrous.pacman.controller.ghost.InkyController
import com.syrous.pacman.controller.ghost.PinkyController
import com.syrous.pacman.controller.pacman.PacmanController
import com.syrous.pacman.controller.pacman.PacmanControllerImpl
import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.GameEvent
import com.syrous.pacman.model.GameInternalEvent.PacmanAteFood
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.GhostMode.CHASING
import com.syrous.pacman.model.GhostMode.FLEEING
import com.syrous.pacman.model.GhostMode.PATROLLING
import com.syrous.pacman.model.Inky
import com.syrous.pacman.model.Pinky
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.ENERGIZER_POSITION
import com.syrous.pacman.util.HORIZONTAL_WALL_LIST
import com.syrous.pacman.util.PATHS
import com.syrous.pacman.util.PATH_WITHOUT_FOOD
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.VERTICAL_WALL_LIST
import com.syrous.pacman.util.cageForceTime
import com.syrous.pacman.util.elroyDotsLeftPart1
import com.syrous.pacman.util.elroyDotsLeftPart2
import com.syrous.pacman.util.elroySpeedPart1
import com.syrous.pacman.util.elroySpeedPart2
import com.syrous.pacman.util.foodEatingFrightSpeed
import com.syrous.pacman.util.foodEatingSpeed
import com.syrous.pacman.util.frightTime
import com.syrous.pacman.util.ghostModeSwitchTimes
import com.syrous.pacman.util.ghostSpeed
import com.syrous.pacman.util.playerFrightSpeed
import com.syrous.pacman.util.playerSpeed
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import kotlin.math.floor

class GameStateImpl : GameState {

    private var screenWidth = 0
    private var screenHeight = 0
    private var gameWidth = 0
    private var gameHeight = 0
    private var scaleFactorX = 0
    private var scaleFactorY = 0
    private var totalFood = 0
    private var foodEaten = 0
    private var ghostLeavingCage = false
    private var mainGhostMode = GhostMode.NONE
    private var lastMainGhostMode = GhostMode.NONE
    private var modeScoreMultiplier = 0
    private var gamePlayMode: GamePlayMode = GamePlayMode.NEWGAME_STARTING
    private var intervalTime = 0
    private val intervalSpeedTable = mutableMapOf<Float, BooleanArray>()
    private var DEFAULT_FPS = 0
    private var currentPlayerSpeed = 0f
    private var currentFoodEatingSpeed = 0f
    private var level = 0
    override val lives: MutableStateFlow<Int> = MutableStateFlow(0)
    private var frightModeTime = 0.0
    private var ghostModeTime = 0.0
    private var forceLeaveCageTime = 0
    private var ghostModeSwitchPos = 0
    private var cruiseElroySpeed = 0f

    private val playField: MutableMap<Int, MutableMap<Int, Tile>> = mutableMapOf()
    private val pacmanController: PacmanController =
        PacmanControllerImpl(this) { gameInternalEvent ->
            when (gameInternalEvent) {
                is PacmanAteFood -> haveFood(gameInternalEvent.playFieldTile)
            }
        }
    private val blinkyController = BlinkyController(this)
    private val pinkyController = PinkyController(this)
    private val inkyController = InkyController(this)
    private val clydeController = ClydeController(this)
    private val ghostControllerList =
        listOf(blinkyController, pinkyController, inkyController, clydeController)

    override val pacman = pacmanController.pacman
    override val hWallList = MutableStateFlow(hashMapOf<Pair<Float, Float>, Pair<Float, Float>>())
    override val vWallList = MutableStateFlow(hashMapOf<Pair<Float, Float>, Pair<Float, Float>>())
    override val foodList = MutableStateFlow<MutableMap<Int, MutableMap<Int, Food>>>(mutableMapOf())
    override var score = MutableStateFlow(0)

    override val blinky: StateFlow<Blinky> = blinkyController.ghost
    override val pinky: StateFlow<Pinky> = pinkyController.ghost
    override val inky: StateFlow<Inky> = inkyController.ghost
    override val clyde: StateFlow<Clyde> = clydeController.ghost

    override val isPaused = MutableStateFlow(false)
    override val gameEvent = MutableSharedFlow<GameEvent>()
    override fun startGamePlay() {
        score.value = 0
        lives.value = 3
        level = 0
        currentPlayerSpeed = playerSpeed
        restartGamePlay()
    }

    private fun restartGamePlay() {
        frightModeTime = 0.0
        intervalTime = 0
        ghostModeSwitchPos = 0
        ghostModeTime = ghostModeSwitchTimes[0] * DEFAULT_FPS
    }

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
            resetForceCageLeaveTime()
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
    }

    private fun buildPacmanWalls() {
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
        vWallList.value = vWallPointList
        hWallList.value = hWallPointList
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

    private fun prepareTile(
        x: Int,
        y: Int,
        tunnel: Boolean,
        isIntersection: Boolean = false,
        food: Food
    ): Tile {
        return Tile(
            isPath = true,
            isTunnel = tunnel,
            isIntersection = isIntersection,
            food = if (food == Food.NONE) Food.PELLET else playField[x]!![y]!!.food
        )
    }

    private fun preparePaths() {
        for (p in PATHS) {
            val startX = p.x * UnitScale
            val startY = p.y * UnitScale
            if (p.horizontalLength > 0) {
                val endX = (p.x + p.horizontalLength - 1) * UnitScale
                val y = p.y * UnitScale

                for (x in p.x * UnitScale until endX step UnitScale) {
                    playField[x]!![y] = prepareTile(
                        x,
                        y,
                        tunnel = (!p.tunnel || x != p.x * UnitScale && x != (p.x + p.horizontalLength - 1) * UnitScale) && p.tunnel,
                        food = playField[x]!![y]!!.food
                    )
                }

                playField[startX]!![y] = playField[startX]!![y]!!.copy(isIntersection = true)
                playField[endX]!![y] = playField[endX]!![y]!!.copy(isIntersection = true)
                Timber.d("Printing Intersection -> ${Pair(startX, y)} & ${Pair(endX, y)}")
            } else {
                val endY = (p.y + p.verticalLength - 1) * UnitScale
                val x = p.x * UnitScale

                for (y in p.y * UnitScale..endY step UnitScale) {
                    playField[x]!![y] = prepareTile(
                        x,
                        y,
                        tunnel = (!p.tunnel || x != p.x * UnitScale && x != (p.x + p.verticalLength - 1) * UnitScale) && p.tunnel,
                        food = playField[x]!![y]!!.food
                    )
                }

                playField[x]!![startY] = playField[x]!![startY]!!.copy(isIntersection = true)
                playField[x]!![endY] = playField[x]!![endY]!!.copy(isIntersection = true)
                Timber.d("Printing Intersection -> ${Pair(x, startY)} & ${Pair(x, endY)}")

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
        changeGamePlayMode(GamePlayMode.NEWGAME_STARTED)
    }

    private fun initializeGhosts() {
        ghostControllerList.forEach {
            it.init(playField, scaleFactorX / UnitScale, scaleFactorY / UnitScale)
        }
        switchMainGhostMode(PATROLLING, true)
        for (ghost in 1 until ghostControllerList.size) {
            ghostControllerList[ghost].switchGhostMode(GhostMode.IN_CAGE)
        }
    }

    private fun switchMainGhostMode(mode: GhostMode, justRestartGame: Boolean) {
        if (mode == FLEEING && frightTime == 0) {
            for (ghost in ghostControllerList) {
                ghost.setReverseDirectionNext(true) // If frightTime is 0, a frightened ghost only reverse its direction.
            }
        } else {
            val oldMainGhostMode = mainGhostMode
            if (mode == FLEEING && mainGhostMode != FLEEING) {
                lastMainGhostMode = mainGhostMode
            }
            mainGhostMode = mode
            when (mode) {
                CHASING, PATROLLING -> {
                    currentPlayerSpeed = playerSpeed * 0.8f
                    currentFoodEatingSpeed = foodEatingSpeed * 0.8f
                }

                FLEEING -> {
                    currentPlayerSpeed = playerFrightSpeed * 0.8f
                    currentFoodEatingSpeed = foodEatingFrightSpeed * 0.8f
                    modeScoreMultiplier = 1
                }

                else -> {}
            }
            for (ghost in ghostControllerList) {
                if (mode != GhostMode.ENTERING_CAGE && !justRestartGame) {
                    ghost.setModeChangedWhileInCage(true)
                }
                if (mode == FLEEING) {
                    ghost.setModeChangedWhileInCage(false)
                }
                if (ghost.getGhostMode() != GhostMode.EATEN &&
                    ghost.getGhostMode() != GhostMode.IN_CAGE &&
                    ghost.getGhostMode() != GhostMode.LEAVING_CAGE &&
                    ghost.getGhostMode() != GhostMode.RE_LEAVING_CAGE &&
                    ghost.getGhostMode() != GhostMode.ENTERING_CAGE ||
                    justRestartGame
                ) {

                    // If it is not immediately after restart the game (justRestartGame:false),
                    // a ghost reverse its direction
                    // when its mode change from other than FRIGHTENED (CHASE or SCATTER) to another mode.
                    if (!justRestartGame && ghost.getGhostMode() != FLEEING && ghost.getGhostMode() != mode) {
                        ghost.setReverseDirectionNext(true)
                    }

                    // If it is not immediately after restart the game
                    // and a mode of each ghost is any of EATEN, IN_PEN, LEAVING_PEN, RE_LEAVING_FROM_PEN, or ENTERING_PEN,
                    // it is not updated.
                    ghost.switchGhostMode(mode)
                }
            }
            pacmanController.setFullSpeed(currentPlayerSpeed)
            pacmanController.setDotEatingSpeed(currentFoodEatingSpeed)
            pacmanController.changeCurrentSpeed()
        }
    }

    private fun createFood() {
        val food = foodList.value
        val scaleFactorX = (screenWidth / (gameWidth * UnitScale))
        val scaleFactorY = (screenHeight / (gameHeight * UnitScale))
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
        pacmanController.move()
        for (ghost in ghostControllerList) {
            ghost.move()
        }
    }

    override fun updateIntervalTime(intervalTime: Int) {
        this.intervalTime = intervalTime
    }

    override fun updateDefaultFps(fps: Int) {
        DEFAULT_FPS = fps
    }

    override fun getSpeedIntervals(speed: Float): BooleanArray {
        if (intervalSpeedTable.containsKey(speed).not()) {
            var distance = 0f
            var lastPos = 0f
            val speedTable = mutableListOf<Boolean>()
            for (i in 0 until DEFAULT_FPS) {
                distance += speed
                val pos = floor(distance)
                if (pos > lastPos) {
                    speedTable.add(true)
                    lastPos = pos
                } else {
                    speedTable.add(false)
                }
            }
            intervalSpeedTable[speed] = speedTable.toBooleanArray()
        }
        return intervalSpeedTable[speed]!!
    }

    override fun getIntervalTime(): Int {
        return intervalTime
    }

    override fun updateTargetPosAfterLoop() {
        for (ghost in ghostControllerList) {
            ghost.updateTargetPos()
        }
    }

    override fun handleTimers() {
        handleGamePlayModeTimer()
        handleGhostModeTimer()
        handleForceLeaveCageTimer()
    }

    private fun handleGhostModeTimer() {
        if (frightModeTime != 0.0) {
            frightModeTime -= 1
            if (frightModeTime <= 0) {
                frightModeTime = 0.0
                finishFrightMode()
            }
        } else if (ghostModeTime > 0) {
            ghostModeTime -= 1
            if (ghostModeTime <= 0) {
                ghostModeTime = 0.0
                ghostModeSwitchPos += 1
                if (ghostModeSwitchPos < ghostModeSwitchTimes.size) {
                    ghostModeTime = ghostModeSwitchTimes[ghostModeSwitchPos] * DEFAULT_FPS
                    when (mainGhostMode) {
                        PATROLLING -> switchMainGhostMode(CHASING, false)
                        CHASING -> switchMainGhostMode(PATROLLING, false)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun finishFrightMode() {
        switchMainGhostMode(lastMainGhostMode, false)
    }

    private fun handleForceLeaveCageTimer() {
        if (forceLeaveCageTime != 0) {
            forceLeaveCageTime -= 1
            if (forceLeaveCageTime <= 0) {
                for (i in 1..3) {
                    if (ghostControllerList[i].getGhostMode() == GhostMode.IN_CAGE) {
                        ghostControllerList[i].setFreeToLeaveCage(true)
                        break
                    }
                }
                resetForceCageLeaveTime()
            }
        }
    }

    private fun resetForceCageLeaveTime() {
        forceLeaveCageTime = cageForceTime * DEFAULT_FPS
    }

    private fun handleGamePlayModeTimer() {
        when (gamePlayMode) {
            GamePlayMode.GHOST_DIED -> {}
            GamePlayMode.PLAYER_DYING -> {}
            GamePlayMode.PLAYER_DIED -> {}
            GamePlayMode.NEWGAME_STARTING -> {
            }

            GamePlayMode.NEWGAME_STARTED, GamePlayMode.GAME_RESTARTED -> {
                changeGamePlayMode(GamePlayMode.ORDINARY_PLAYING)
            }

            GamePlayMode.GAME_RESTARTING -> {
                changeGamePlayMode(GamePlayMode.GAME_RESTARTED)
            }

            GamePlayMode.GAMEOVER -> TODO()
            GamePlayMode.LEVEL_BEING_COMPLETED -> {}
            GamePlayMode.LEVEL_COMPLETED -> TODO()
            GamePlayMode.TRANSITION_INTO_NEXT_SCENE -> TODO()
            else -> {}
        }
    }

    private fun changeGamePlayMode(mode: GamePlayMode) {
        gamePlayMode = mode
//        when(mode) {
//            GamePlayMode.ORDINARY_PLAYING -> TODO()
//            GamePlayMode.GHOST_DIED -> TODO()
//            GamePlayMode.PLAYER_DYING -> TODO()
//            GamePlayMode.PLAYER_DIED -> TODO()
//            GamePlayMode.NEWGAME_STARTING -> TODO()
//            GamePlayMode.NEWGAME_STARTED -> TODO()
//            GamePlayMode.GAME_RESTARTING -> TODO()
//            GamePlayMode.GAME_RESTARTED -> TODO()
//            GamePlayMode.GAMEOVER -> TODO()
//            GamePlayMode.LEVEL_BEING_COMPLETED -> TODO()
//            GamePlayMode.LEVEL_COMPLETED -> TODO()
//            GamePlayMode.TRANSITION_INTO_NEXT_SCENE -> TODO()
//            GamePlayMode.CUTSCENE -> TODO()
//            GamePlayMode.KILL_SCREEN -> TODO()
//        }
    }


    private fun updateScore(food: Food) {
        foodEaten += 1
        var newScore = score.value
        when (food) {
            Food.NONE -> {}
            Food.PELLET -> {
                newScore += 10
            }

            Food.ENERGIZER -> {
                switchMainGhostMode(FLEEING, false)
                newScore += 50
            }
        }
        updateCruiseElroySpeed()
        resetForceCageLeaveTime()
        score.value = newScore
    }

    private fun haveFood(playFieldTile: Pair<Int, Int>) {
        val adjustedScaleFactorX = scaleFactorX / UnitScale
        val adjustedScaleFactorY = scaleFactorY / UnitScale
        val listOfAvailableFood = foodList.value.toMutableMap()
        updateScore(playField[playFieldTile.first]!![playFieldTile.second]!!.food)
        playField[playFieldTile.first]!![playFieldTile.second] =
            playField[playFieldTile.first]!![playFieldTile.second]!!.copy(food = Food.NONE)
        listOfAvailableFood[playFieldTile.first * adjustedScaleFactorX]!!.remove(playFieldTile.second * adjustedScaleFactorY)
        pacmanController.updatePlayField(playField)
        foodList.value = listOfAvailableFood
    }

    override fun updateCruiseElroySpeed() {
        var speed = ghostSpeed * 0.8f
        if (clydeController.getGhostMode() != GhostMode.IN_CAGE) {
            if (totalFood - foodEaten < elroyDotsLeftPart2) {
                speed = elroySpeedPart2 * 0.8f
            } else if (totalFood - foodEaten < elroyDotsLeftPart1) {
                speed = elroySpeedPart1 * 0.8f
            }
        }
        if (speed != cruiseElroySpeed) {
            cruiseElroySpeed = speed
            blinkyController.changeCurrentSpeed() // update the speed of Blinky.
        }
    }

    override fun pauseGame() {
        isPaused.value = true
    }

    override fun resumeGame() {
        isPaused.value = false
    }

    override fun moveUp() {
        pacmanController.moveUp()
    }

    override fun moveDown() {
        pacmanController.moveDown()
    }

    override fun moveLeft() {
        pacmanController.moveLeft()
    }

    override fun moveRight() {
        pacmanController.moveRight()
    }

    override fun isGhostExitingCage(): Boolean {
        return ghostLeavingCage
    }

    override fun setGhostExitingCage(ghostExitingCageNow: Boolean) {
        ghostLeavingCage = ghostExitingCageNow
    }

    override fun getMainGhostMain(): GhostMode {
        return mainGhostMode
    }

    override fun getLastMainGhostMode(): GhostMode {
        return lastMainGhostMode
    }

    override fun getGamePlayMode(): GamePlayMode {
        return gamePlayMode
    }

    override fun getCruiseElroySpeed(): Float {
        return elroySpeedPart1
    }

    private fun initializePacman() {
        pacmanController.init(playField, scaleFactorX / UnitScale, scaleFactorY / UnitScale)
    }
}
