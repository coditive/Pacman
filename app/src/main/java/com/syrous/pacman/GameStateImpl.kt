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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

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

    private val playField: MutableMap<Int, MutableMap<Int, Tile>> = mutableMapOf()
    private val pacmanController: PacmanController = PacmanControllerImpl { gameInternalEvent ->
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
    override val score = MutableStateFlow(0)

    override val blinky: StateFlow<Blinky> = blinkyController.ghost
    override val pinky: StateFlow<Pinky> = pinkyController.ghost
    override val inky: StateFlow<Inky> = inkyController.ghost
    override val clyde: StateFlow<Clyde> = clydeController.ghost

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
            food = if (playField[x]!![y]!!.food == Food.NONE) Food.PELLET else playField[x]!![y]!!.food
        )
    }

    private fun preparePaths() {
        for (p in PATHS) {
            val startX = p.x * UnitScale
            val startY = p.y * UnitScale
            if (p.horizontalLength > 0) {
                val endX = (p.x + p.horizontalLength - 1) * UnitScale
                val y = p.y * UnitScale

                for (x in (p.x + 1) * UnitScale until endX step UnitScale) {
                    playField[x]!![y] = prepareTile(x, y, p.tunnel, food = playField[x]!![y]!!.food)
                }

                playField[startX]!![y] =
                    prepareTile(
                        startX,
                        y,
                        p.tunnel,
                        isIntersection = true,
                        food = playField[startX]!![y]!!.food
                    )

                playField[endX]!![y] =
                    prepareTile(
                        endX,
                        y,
                        p.tunnel,
                        isIntersection = true,
                        playField[endX]!![y]!!.food
                    )
            } else {
                val endY = (p.y + p.verticalLength - 1) * UnitScale
                val x = p.x * UnitScale

                for (y in (p.y + 1) * UnitScale..endY step UnitScale) {
                    playField[x]!![y] = prepareTile(x, y, p.tunnel, food = playField[x]!![y]!!.food)
                }

                playField[x]!![startY] =
                    prepareTile(
                        x,
                        startY,
                        p.tunnel,
                        isIntersection = true,
                        playField[x]!![startY]!!.food
                    )

                playField[x]!![endY] =
                    prepareTile(
                        x,
                        endY,
                        p.tunnel,
                        isIntersection = true,
                        playField[x]!![endY]!!.food
                    )
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

        Timber.d("Playfield -> $playField")

    }

    private fun createPlayFieldElements() {
        createFood()
        initializePacman()
        initializeGhosts()
    }

    private fun initializeGhosts() {
        ghostControllerList.forEach {
            it.init(playField, scaleFactorX / UnitScale, scaleFactorY / UnitScale)
        }
        switchMainGhostMode(PATROLLING, false)
        for (ghost in 1 until ghostControllerList.size) {
            ghostControllerList[ghost].switchGhostMode(GhostMode.IN_CAGE)
        }
    }

    private fun switchMainGhostMode(mode: GhostMode, justRestartGame: Boolean) {
        if (mode == GhostMode.FLEEING) {
            for (ghost in ghostControllerList) {
                ghost.setReverseDirectionNext(true) // If frightTime is 0, a frightened ghost only reverse its direction.
            }
        } else {
            val oldMainGhostMode = mainGhostMode
            if (mode == GhostMode.FLEEING && mainGhostMode != GhostMode.FLEEING) {
                lastMainGhostMode = mainGhostMode
            }
            mainGhostMode = mode
            if (mode == GhostMode.FLEEING || oldMainGhostMode == GhostMode.FLEEING) {

            }
            when (mode) {
                CHASING, PATROLLING -> {

                }

                FLEEING -> {
                    modeScoreMultiplier = 1
                }

                else -> {}
            }
            for (ghost in ghostControllerList) {
                if (mode != GhostMode.ENTERING_CAGE && !justRestartGame) {
                    ghost.setModeChangedWhileInCage(true)
                }
                if (mode == GhostMode.FLEEING) {
                    ghost.setModeChangedWhileInCage(false)
                }
                if (ghost.getGhostMode() != GhostMode.EATEN && ghost.getGhostMode() != GhostMode.IN_CAGE && ghost.getGhostMode() !== GhostMode.LEAVING_CAGE && ghost.getGhostMode() !== GhostMode.RE_LEAVING_CAGE && ghost.getGhostMode() !== GhostMode.ENTERING_CAGE || justRestartGame) {

                    // If it is not immediately after restart the game (justRestartGame:false),
                    // a ghost reverse its direction
                    // when its mode change from other than FRIGHTENED (CHASE or SCATTER) to another mode.
                    if (!justRestartGame && ghost.getGhostMode() != GhostMode.FLEEING && ghost.getGhostMode() !== mode) {
                        ghost.setReverseDirectionNext(true)
                    }

                    // If it is not immediately after restart the game
                    // and a mode of each ghost is any of EATEN, IN_PEN, LEAVING_PEN, RE_LEAVING_FROM_PEN, or ENTERING_PEN,
                    // it is not updated.
                    ghost.switchGhostMode(mode)
                }
            }

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

    override fun handleTimers() {
        handleGamePlayModeTimer()
    }

    private fun handleGamePlayModeTimer() {
        when (gamePlayMode) {
            GamePlayMode.GHOST_DIED -> {}
            GamePlayMode.PLAYER_DYING -> {}
            GamePlayMode.PLAYER_DIED -> {}
            GamePlayMode.NEWGAME_STARTING -> {
                changeGamePlayMode(GamePlayMode.NEWGAME_STARTED)
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
        score.value = foodEaten
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

    private fun initializePacman() {
        pacmanController.init(playField, scaleFactorX / UnitScale, scaleFactorY / UnitScale)
    }
}
