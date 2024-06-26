package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.controller.ActorController
import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.Inky
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Pinky
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.getOppositeDir
import com.syrous.pacman.util.CAGE_ENTRANCE_TILE
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.getEuclideanDistanceBetweenFloat
import com.syrous.pacman.util.plus

abstract class GhostController(
    private val gameState: GameState
) : ActorController(gameState) {

    protected var followingRoutine: Boolean = true
    private var proceedToNextRoutine: Boolean = false
    private var routineMoveId = 0
    protected var reverseDirectionsNext = false
    protected var mode: GhostMode = GhostMode.NONE
    private var freeToExitCage = false
    private var eatenInFrightenedMode = false
    protected var scaleFactorX = 0
    protected var scaleFactorY = 0
    protected var ghostModeChangedInCage = false
    protected var targetPos: Pair<Float, Float> = Pair(0f, 0f)
    protected var scatterPos: Pair<Float, Float> = Pair(0f, 0f)

    abstract fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int)

    fun followRoutine() {
        if (routineMoveId == -1 || proceedToNextRoutine) {
            switchFollowingRoutine()
        }
        continueFollowingRoutine()
    }

    private fun switchFollowingRoutine() {
        this.routineMoveId += 1
        if (this.routineMoveId == getMovesInCage().size) {
            when {
                mode == GhostMode.IN_CAGE && freeToExitCage &&
                        gameState.isGhostExitingCage().not() -> {
                    switchGhostMode(
                        if (eatenInFrightenedMode) GhostMode.RE_LEAVING_CAGE else GhostMode.LEAVING_CAGE,
                    )
                    return
                }

                mode == GhostMode.LEAVING_CAGE || mode == GhostMode.RE_LEAVING_CAGE -> {
                    var ghostMode = gameState.getMainGhostMain()
                    val oldActor = actor as Ghost
                    actor = when(oldActor) {
                        is Blinky -> oldActor.copy(position = Pair(CAGE_ENTRANCE_TILE.first * UnitScale, CAGE_ENTRANCE_TILE.second * UnitScale))
                        is Clyde -> oldActor.copy(position = Pair(CAGE_ENTRANCE_TILE.first * UnitScale, CAGE_ENTRANCE_TILE.second * UnitScale))
                        is Inky -> oldActor.copy(position = Pair(CAGE_ENTRANCE_TILE.first * UnitScale, CAGE_ENTRANCE_TILE.second * UnitScale))
                        is Pinky -> oldActor.copy(position = Pair(CAGE_ENTRANCE_TILE.first * UnitScale, CAGE_ENTRANCE_TILE.second * UnitScale))
                    }
                    if (mode == GhostMode.RE_LEAVING_CAGE && ghostMode == GhostMode.FLEEING) {
                        ghostMode = gameState.getLastMainGhostMode()
                    }
                    switchGhostMode(ghostMode)
                    return
                }

                mode == GhostMode.ENTERING_CAGE -> {
                    if (this == BlinkyController::class || freeToExitCage) {
                        switchGhostMode(GhostMode.RE_LEAVING_CAGE)
                    } else {
                        eatenInFrightenedMode = true
                        switchGhostMode(GhostMode.IN_CAGE)
                    }
                    return
                }

                else -> {
                    routineMoveId = 0
                }
            }

            val move = getMovesInCage()[routineMoveId]
            val ghostX = move.x * UnitScale
            val ghostY = move.y * UnitScale
            val dir = move.direction
            intervalSpeedTable = gameState.getSpeedIntervals(move.speed)

            proceedToNextRoutine = false
            updateActor(
                ActorUpdateInfo(
                    position = Pair(ghostX, ghostY),
                    tilePos = actor.tilePos,
                    lastGoodTilePos = actor.lastGoodTilePos,
                    direction = dir,
                    lastActiveDir = actor.lastActiveDir,
                    nextDir = actor.nextDir,
                    physicalSpeed = actor.physicalSpeed,
                    speed = actor.speed,
                    fullSpeed = actor.fullSpeed,
                    tunnelSpeed = actor.tunnelSpeed
                )
            )
        }
    }

    private fun continueFollowingRoutine() {
        var move: MoveInCage? = null
        if (routineMoveId >= 0 && routineMoveId < getMovesInCage().size) {
            move = getMovesInCage()[routineMoveId]
        }
        if (move != null) {
            if (intervalSpeedTable[gameState.getIntervalTime()]) {
                val dir = actor.direction
                val pos = actor.position
                var newPos = pos + dir.move

                when (dir) {
                    Directions.NONE -> {}
                    Directions.LEFT -> {
                        if (newPos.first < move.dest * UnitScale) {
                            newPos = Pair(move.dest * UnitScale, newPos.second)
                            proceedToNextRoutine = true
                        }
                    }

                    Directions.UP -> {
                        if (newPos.second < move.dest * UnitScale) {
                            newPos = Pair(newPos.first, move.dest * UnitScale)
                            proceedToNextRoutine = true
                        }
                    }

                    Directions.RIGHT -> {
                        if (newPos.first > move.dest * UnitScale) {
                            newPos = Pair(move.dest * UnitScale, newPos.second)
                            proceedToNextRoutine = true
                        }
                    }

                    Directions.DOWN -> {
                        if (newPos.second > move.dest * UnitScale) {
                            newPos = Pair(newPos.first, move.dest * UnitScale)
                            proceedToNextRoutine = true
                        }
                    }
                }

                updateActor(
                    ActorUpdateInfo(
                        position = newPos,
                        tilePos = actor.tilePos,
                        lastGoodTilePos = actor.lastGoodTilePos,
                        direction = actor.direction,
                        lastActiveDir = actor.lastActiveDir,
                        nextDir = actor.nextDir,
                        physicalSpeed = actor.physicalSpeed,
                        speed = actor.speed,
                        fullSpeed = actor.fullSpeed,
                        tunnelSpeed = actor.tunnelSpeed
                    )
                )
            }
        }
    }

    fun switchGhostMode(mode: GhostMode) {
        val oldMode = this.mode
        this.mode = mode
        if(actor is Clyde || (mode == GhostMode.IN_CAGE && oldMode == GhostMode.IN_CAGE)) {
            gameState.updateCruiseElroySpeed()
        }
        when (oldMode) {
            GhostMode.NONE,
            GhostMode.PATROLLING,
            GhostMode.CHASING,
            GhostMode.FLEEING,
            GhostMode.ENTERING_CAGE,
            GhostMode.IN_CAGE,
            GhostMode.RE_LEAVING_CAGE -> {
            }

            GhostMode.EATEN -> {

            }

            GhostMode.LEAVING_CAGE -> {
                gameState.setGhostExitingCage(true)
            }
        }

        when (mode) {
            GhostMode.NONE -> {}
            GhostMode.PATROLLING -> {
                targetPos = scatterPos
                followingRoutine = false
            }

            GhostMode.CHASING -> {
                followingRoutine = false
            }

            GhostMode.FLEEING -> {
                followingRoutine = false
            }

            GhostMode.EATEN -> {
                targetPos = Pair(
                    CAGE_ENTRANCE_TILE.first * scaleFactorX.toFloat(),
                    CAGE_ENTRANCE_TILE.second * scaleFactorY.toFloat()
                )
                freeToExitCage = false
                followingRoutine = false
            }

            GhostMode.LEAVING_CAGE -> {
                followingRoutine = true
                routineMoveId = -1
                gameState.setGhostExitingCage(true)
            }

            GhostMode.IN_CAGE,
            GhostMode.ENTERING_CAGE,
            GhostMode.RE_LEAVING_CAGE -> {
                followingRoutine = true
                routineMoveId = -1
            }
        }
        changeCurrentSpeed()
    }

    private fun decideNextDir(
        ghost: Ghost,
        reversed: Boolean
    ) {
        val currentTile = ghost.tilePos
        val dir = ghost.direction
        var nextDir = ghost.nextDir
        val newTile = Pair(
            currentTile.first + dir.move.first.toInt(),
            currentTile.second + dir.move.second.toInt()
        )
        var destination = getPlayFieldTile(newTile)
        if (reversed && destination.isIntersection.not()) {
            destination = getPlayFieldTile(currentTile)
        }
        if (destination.isIntersection) {
            when (this.mode) {
                GhostMode.PATROLLING,
                GhostMode.CHASING,
                GhostMode.EATEN -> {
                    if (destination.allowedOnlyOpposite(dir)) {
                        nextDir = dir.getOppositeDir()
                    } else {
                        var minDist = Float.MAX_VALUE
                        var preferredDir = Directions.NONE
                        for (d in destination.allowedDir) {
                            if (dir != d.getOppositeDir()) {
                                val newTileCandidate = ghost.position + d.move
                                val distance =
                                    getEuclideanDistanceBetweenFloat(newTileCandidate, targetPos)
                                if (minDist > distance) {
                                    preferredDir = d
                                    minDist = distance
                                }
                            }
                        }

                        if (preferredDir != Directions.NONE) {
                            nextDir = preferredDir
                        }
                    }
                    actor = when (ghost) {
                        is Blinky -> ghost.copy(nextDir = nextDir)
                        is Clyde -> ghost.copy(nextDir = nextDir)
                        is Inky -> ghost.copy(nextDir = nextDir)
                        is Pinky -> ghost.copy(nextDir = nextDir)
                    }
                }

                GhostMode.FLEEING -> {
                    var newDir: Directions
                    do {
                        newDir = Directions.entries.random()
                    } while (destination.allowedDir.contains(newDir).not()
                        || newDir == dir.getOppositeDir()
                    )

                    actor = when (ghost) {
                        is Blinky -> ghost.copy(nextDir = nextDir)
                        is Clyde -> ghost.copy(nextDir = nextDir)
                        is Inky -> ghost.copy(nextDir = nextDir)
                        is Pinky -> ghost.copy(nextDir = nextDir)
                    }
                }

                else -> {}
            }
        }

    }

    open fun getNormalSpeed(): Float = actor.fullSpeed

    abstract fun setReverseDirectionNext(reversed: Boolean)

    fun setModeChangedWhileInCage(change: Boolean) {
        ghostModeChangedInCage = change
    }

    fun setFreeToLeaveCage(toLeave: Boolean) {
        freeToExitCage = toLeave
    }

    fun getGhostMode(): GhostMode = mode

    abstract fun updateTargetPos()

    abstract fun getMovesInCage(): List<MoveInCage>

    override fun adjustOverShootOnEnteringTile(
        tilePos: Pair<Int, Int>,
        actor: Actor
    ) {
    }

    override fun reverseOnEnteringTile(actor: Actor) {
        if (this.reverseDirectionsNext) { // reverse its direction
            if (actor is Ghost) {
                val dir = actor.direction.getOppositeDir()
                val nextDir = Directions.NONE
                this.reverseDirectionsNext = false
                val newActor = when (actor) {
                    is Blinky -> {
                        this.actor = actor.copy(direction = dir, nextDir = nextDir)
                        actor.copy(direction = dir, nextDir = nextDir)
                    }

                    is Clyde -> {
                        this.actor = actor.copy(direction = dir, nextDir = nextDir)
                        actor.copy(direction = dir, nextDir = nextDir)
                    }

                    is Inky -> {
                        this.actor = actor.copy(direction = dir, nextDir = nextDir)
                        actor.copy(direction = dir, nextDir = nextDir)
                    }

                    is Pinky -> {
                        this.actor = actor.copy(direction = dir, nextDir = nextDir)
                        actor.copy(direction = dir, nextDir = nextDir)
                    }
                }
                decideNextDir(newActor, reversed = true)
            }
        }
    }

    override fun handleObjectOnEncounter(actor: Actor) {
    }

    override fun decideNextDirAfterEnteredTile(
        actor: Actor
    ) {
        if (actor is Ghost) {
            decideNextDir(ghost = actor, reversed = false)
        }
    }

    override fun haveFood(tilePos: Pair<Int, Int>) {
    }
}