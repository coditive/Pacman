package com.syrous.pacman.controller

import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GameInternalEvent
import com.syrous.pacman.model.GameInternalEvent.PacmanAteFood
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.minus
import com.syrous.pacman.util.plus
import com.syrous.pacman.util.times
import com.syrous.pacman.util.toFloat
import com.syrous.pacman.util.toGamePos
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class PacmanControllerImpl(
    private val gameEventCallback: (GameInternalEvent) -> Unit
) : ActorController(), PacmanController {

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
        )
    )

    override fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int) {
        this.playField = playField
        this.scaleFactorX = scaleFactorX
        this.scaleFactorY = scaleFactorY
        pacman.value = Pacman(
            position = Pair(14f * UnitScale, 24f * UnitScale),
            tilePos = Pair(14, 24),
            screenPos = Pair(14f * scaleFactorX, 24f * scaleFactorY),
            lastGoodTilePos = Pair(14, 24),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT
        )
    }

    override fun updatePlayField(playField: Map<Int, Map<Int, Tile>>) {
        this.playField = playField
    }

    override fun move() {
        if (requestedChangeDir != Directions.NONE) {
            handleDirectionChange(requestedChangeDir)
            requestedChangeDir = Directions.NONE
        }
        step(pacman.value) { actorUpdateInfo ->
            pacman.value = pacman.value.copy(
                position = actorUpdateInfo.position,
                tilePos = actorUpdateInfo.tilePos,
                screenPos = Pair(
                    actorUpdateInfo.position.first * scaleFactorX,
                    actorUpdateInfo.position.second * scaleFactorY
                ),
                lastGoodTilePos = actorUpdateInfo.lastGoodTilePos,
                lastActiveDir = actorUpdateInfo.lastActiveDir,
                direction = actorUpdateInfo.direction,
                nextDir = actorUpdateInfo.nextDir
            )
        }
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
        playFieldTile: Pair<Int, Int>,
        updateActor: (ActorUpdateInfo) -> Unit
    ) {
        if (playField[playFieldTile.first]!![playFieldTile.second]!!.isPath.not()) {
            val lastGoodTile = pacman.value.lastGoodTilePos
            Timber.d("adjustOverShoot playField ->  ${playField[playFieldTile.first]!![playFieldTile.second]!!}, updateActor -> ${ActorUpdateInfo(
                position = Pair(
                    lastGoodTile.first * UnitScale.toFloat(),
                    lastGoodTile.second * UnitScale.toFloat()
                ),
                tilePos = lastGoodTile,
                lastGoodTilePos = pacman.value.lastGoodTilePos,
                direction = Directions.NONE,
                lastActiveDir = pacman.value.lastActiveDir,
                nextDir = pacman.value.nextDir,
            )}")

        }
    }

    override fun reverseOnEnteringTile() {
    }

    override fun handleObjectOnEncounter() {
    }

    override fun decideNextDirAfterEnteredTile() {
    }

    override fun haveFood(tilePos: Pair<Int, Int>) {
        gameEventCallback(PacmanAteFood(tilePos))
    }

    private fun handleDirectionChange(inputDir: Directions) {
        var dir = pacman.value.direction
        val tilePos = pacman.value.tilePos
        var lastActiveDir = pacman.value.lastActiveDir
        if (dir == getOppositeDirection(inputDir)) {
            dir = inputDir
            if (dir != Directions.NONE) {
                lastActiveDir = dir
            }
            pacman.value = pacman.value.copy(
                direction = dir,
                nextDir = Directions.NONE,
                lastActiveDir = lastActiveDir
            )
        } else if (dir != inputDir) {
            val tile = getPlayFieldTile(tilePos)
            val playFieldTile = playField[tile.first]!![tile.second]!!
            if (dir == Directions.NONE) {
                if (playFieldTile.allowedDir.contains(inputDir)) {
                    dir = inputDir
                    pacman.value = pacman.value.copy(direction = dir, lastActiveDir = dir)
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
                        pacman.value = pacman.value.copy(
                            position = Pair(
                                newPos.first * UnitScale,
                                newPos.second * UnitScale
                            ),
                            screenPos = Pair(newPos.first * scaleFactorX, newPos.second * scaleFactorY),
                            direction = dir,
                            lastActiveDir = dir
                        )
                        Timber.d("input dir stepPassed != 0 ${pacman.value}")
                        return
                    }
                }
                pacman.value = pacman.value.copy(nextDir = inputDir)
            }
        }
    }
}