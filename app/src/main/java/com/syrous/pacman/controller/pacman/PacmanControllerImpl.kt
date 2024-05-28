package com.syrous.pacman.controller.pacman

import com.syrous.pacman.GameState
import com.syrous.pacman.controller.ActorController
import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.CurrentSpeed
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GameInternalEvent
import com.syrous.pacman.model.GameInternalEvent.PacmanAteFood
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.toPacman
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.foodEatingSpeed
import com.syrous.pacman.util.minus
import com.syrous.pacman.util.playerSpeed
import com.syrous.pacman.util.plus
import com.syrous.pacman.util.times
import com.syrous.pacman.util.toFloat
import com.syrous.pacman.util.toGamePos
import kotlinx.coroutines.flow.MutableStateFlow

class PacmanControllerImpl(
    private val gameState: GameState, private val gameEventCallback: (GameInternalEvent) -> Unit
) : ActorController(gameState), PacmanController {

    private var requestedChangeDir = Directions.NONE
    private var scaleFactorX = 0
    private var scaleFactorY = 0

    override val pacman: MutableStateFlow<Pacman> = MutableStateFlow(
        Pacman(
            position = Pair(0f, 0f),
            tilePos = Pair(0, 0),
            lastGoodTilePos = Pair(0, 0),
            screenPos = Pair(0f, 0f),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT,
            speed = CurrentSpeed.NORMAL,
            physicalSpeed = 0f,
            fullSpeed = 0f,
            tunnelSpeed = 0f,
            dotEatingSpeed = 0f
        )
    )

    override fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int) {
        this.playField = playField
        this.scaleFactorX = scaleFactorX
        this.scaleFactorY = scaleFactorY
        actor = Pacman(
            position = Pair(14f * UnitScale, 24f * UnitScale),
            tilePos = Pair(14, 24),
            screenPos = Pair(14f * scaleFactorX, 24f * scaleFactorY),
            lastGoodTilePos = Pair(14, 24),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT,
            speed = CurrentSpeed.NORMAL,
            physicalSpeed = 0f,
            fullSpeed = playerSpeed,
            tunnelSpeed = playerSpeed,
            dotEatingSpeed = foodEatingSpeed
        )
        pacman.value = Pacman(
            position = Pair(14f * UnitScale, 24f * UnitScale),
            tilePos = Pair(14, 24),
            screenPos = Pair(14f * scaleFactorX, 24f * scaleFactorY),
            lastGoodTilePos = Pair(14, 24),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT,
            speed = CurrentSpeed.NORMAL,
            physicalSpeed = 0f,
            fullSpeed = playerSpeed,
            tunnelSpeed = playerSpeed,
            dotEatingSpeed = foodEatingSpeed
        )
    }

    override fun updatePlayField(playField: Map<Int, Map<Int, Tile>>) {
        this.playField = playField
    }

    override fun move() {
        if (gameState.getGamePlayMode() == GamePlayMode.ORDINARY_PLAYING) {
            if (requestedChangeDir != Directions.NONE) {
                handleDirectionChange(requestedChangeDir)
                requestedChangeDir = Directions.NONE
            }
            step()
        }
    }

    override fun updateActor(actorUpdateInfo: ActorUpdateInfo) {
        pacman.value = actorUpdateInfo.toPacman(
            scaleFactorX,
            scaleFactorY,
            (actor as Pacman).dotEatingSpeed
        )
        actor = actorUpdateInfo.toPacman(
            scaleFactorX,
            scaleFactorY,
            (actor as Pacman).dotEatingSpeed
        )
    }

    override fun moveLeft() {
        requestedChangeDir = Directions.LEFT
    }

    override fun moveRight() {
        requestedChangeDir = Directions.RIGHT
    }

    override fun moveUp() {
        requestedChangeDir = Directions.UP
    }

    override fun moveDown() {
        requestedChangeDir = Directions.DOWN
    }

    override fun adjustOverShootOnEnteringTile(
        tilePos: Pair<Int, Int>, actor: Actor
    ) {
        if (actor is Pacman) {
            if (getPlayFieldTile(tilePos).isPath.not()) {
                val lastGoodTile = pacman.value.lastGoodTilePos
                this.actor = actor.copy(
                    position = Pair(
                        lastGoodTile.first * UnitScale.toFloat(),
                        lastGoodTile.second * UnitScale.toFloat()
                    ),
                    tilePos = lastGoodTile,
                    direction = Directions.NONE,
                )
            }
        }
    }

    override fun reverseOnEnteringTile(actor: Actor) {
    }

    override fun handleObjectOnEncounter(actor: Actor) {
    }

    override fun decideNextDirAfterEnteredTile(actor: Actor) {
    }

    override fun haveFood(tilePos: Pair<Int, Int>) {
        gameEventCallback(PacmanAteFood(tilePos))
    }

    override fun changeCurrentSpeed(speed: CurrentSpeed) {
        actor = pacman.value.copy(speed = speed)
        changeCurrentSpeed()
    }

    override fun changeCurrentSpeed() {
        val s = when (actor.speed) {
            CurrentSpeed.NONE -> 0f
            CurrentSpeed.NORMAL -> actor.fullSpeed
            CurrentSpeed.PACMAN_EATING -> (actor as Pacman).dotEatingSpeed
            CurrentSpeed.PASSING_TUNNEL -> actor.tunnelSpeed
        }
        if (actor.physicalSpeed != s) {
            actor = pacman.value.copy(physicalSpeed = s)
            intervalSpeedTable = gameState.getSpeedIntervals(s)
        }
    }


    private fun handleDirectionChange(inputDir: Directions) {
        var dir = actor.direction
        val tilePos = actor.tilePos
        var lastActiveDir = actor.lastActiveDir
        if (dir == getOppositeDirection(inputDir)) {
            dir = inputDir
            if (dir != Directions.NONE) {
                lastActiveDir = dir
            }
            actor = pacman.value.copy(
                direction = dir, nextDir = Directions.NONE, lastActiveDir = lastActiveDir
            )
        } else if (dir != inputDir) {
            val playFieldTile = getPlayFieldTile(tilePos)
            if (dir == Directions.NONE) {
                if (playFieldTile.allowedDir.contains(inputDir)) {
                    dir = inputDir
                    actor = pacman.value.copy(direction = dir, lastActiveDir = dir)
                }
            } else {
                if (playFieldTile.allowedDir.contains(inputDir)) {
                    var pastPos = pacman.value.position
                    val pastTilePos = pacman.value.tilePos
                    pastPos -= inputDir.move
                    var stepPassed = 0
                    if (pastPos.toGamePos() == pastTilePos) {
                        stepPassed = 1
                    } else {
                        pastPos -= inputDir.move
                        if (pastPos.toGamePos() == pastTilePos) {
                            stepPassed = 2
                        }
                    }

                    if (stepPassed != 0) {
                        // the input of direction is slightly delayed,
                        // correct the location according to the new direction.
                        dir = inputDir
                        val pastTile = pacman.value.tilePos
                        val newPos = pastTile.toFloat() + dir.move * stepPassed
                        actor = pacman.value.copy(
                            position = Pair(
                                newPos.first * UnitScale, newPos.second * UnitScale
                            ), screenPos = Pair(
                                newPos.first * scaleFactorX, newPos.second * scaleFactorY
                            ), direction = dir, lastActiveDir = dir
                        )
                        return
                    }
                }
                actor = pacman.value.copy(nextDir = inputDir)
            }
        }
    }

    override fun setFullSpeed(speed: Float) {
        actor = pacman.value.copy(fullSpeed = speed, tunnelSpeed = speed)
        pacman.value = pacman.value.copy(fullSpeed = speed, tunnelSpeed = speed)
    }

    override fun setDotEatingSpeed(speed: Float) {
        actor = pacman.value.copy(dotEatingSpeed = speed)
        pacman.value = pacman.value.copy(dotEatingSpeed = speed)
    }
}