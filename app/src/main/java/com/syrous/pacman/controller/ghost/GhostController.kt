package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.controller.ActorController
import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.getOppositeDir
import com.syrous.pacman.util.CAGE_ENTRANCE_TILE
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.getEuclideanDistanceBetween
import com.syrous.pacman.util.plus

abstract class GhostController(
    private val gameState: GameState
) : ActorController() {

    protected var followingRoutine: Boolean = true
    private var proceedToNextRoutine: Boolean = false
    private var routineMoveId = 0
    private var reverseDirectionsNext = false
    protected var mode: GhostMode = GhostMode.NONE
    private var freeToExitCage = false
    private var eatenInFrightenedMode = false
    protected var scaleFactorX = 0
    protected var scaleFactorY = 0
    private var targetPos: Pair<Float, Float> = Pair(0f, 0f)
    protected var scatterPos: Pair<Float, Float> = Pair(0f, 0f)

    abstract fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int)

    fun followRoutine(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        if (proceedToNextRoutine) {
            switchFollowingRoutine(actor, updateActor)
        }
        continueFollowingRoutine(actor, updateActor)
    }

    private fun switchFollowingRoutine(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        this.routineMoveId += 1
        if (this.routineMoveId == getMovesInCage().size) {
            when {
                mode == GhostMode.IN_CAGE && freeToExitCage && gameState.isGhostExitingCage()
                    .not() -> {
                    switchGhostMode(
                        if (eatenInFrightenedMode) GhostMode.RE_LEAVING_CAGE else GhostMode.LEAVING_CAGE,
                        actor,
                        updateActor
                    )
                }

                mode == GhostMode.LEAVING_CAGE || mode == GhostMode.RE_LEAVING_CAGE -> {
                    var ghostMode = gameState.getMainGhostMain()
                    if (mode == GhostMode.RE_LEAVING_CAGE && ghostMode == GhostMode.FLEEING) {
                        ghostMode = gameState.getLastMainGhostMode()
                    }
                    switchGhostMode(ghostMode, actor, updateActor)
                }

                mode == GhostMode.ENTERING_CAGE -> {
                    if (this == BlinkyController::class || freeToExitCage) {
                        switchGhostMode(GhostMode.RE_LEAVING_CAGE, actor, updateActor)
                    } else {
                        eatenInFrightenedMode = true
                        switchGhostMode(GhostMode.IN_CAGE, actor, updateActor)
                    }
                }

                else -> {
                    routineMoveId = 0
                    val move = getMovesInCage()[routineMoveId]
                    val ghostX = move.x * UnitScale
                    val ghostY = move.y * UnitScale
                    val dir = move.direction
                    proceedToNextRoutine = false
                    updateActor(
                        ActorUpdateInfo(
                            position = Pair(ghostX, ghostY),
                            tilePos = actor.tilePos,
                            lastGoodTilePos = actor.lastGoodTilePos,
                            direction = dir,
                            lastActiveDir = actor.lastActiveDir,
                            nextDir = actor.nextDir,
                        )
                    )

                }
            }
        }
    }

    private fun continueFollowingRoutine(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        var move: MoveInCage? = null
        if (routineMoveId >= 0 && routineMoveId < getMovesInCage().size) {
            move = getMovesInCage()[routineMoveId]
        }
        if (move != null) {
            val dir = actor.direction
            val pos = actor.position
            var newPos = pos + dir.move
            when (dir) {
                Directions.NONE -> {}
                Directions.LEFT, Directions.RIGHT -> {
                    val moveCageX = (move.x + move.direction.move.first * move.dest) * UnitScale
                    if (newPos.first < moveCageX || newPos.first > moveCageX) {
                        newPos = Pair(moveCageX, newPos.second)
                        proceedToNextRoutine = true
                    }
                }

                Directions.UP, Directions.DOWN -> {
                    val moveCageY = (move.y + move.direction.move.first * move.dest) * UnitScale
                    if (newPos.second < moveCageY || newPos.second > moveCageY) {
                        newPos = Pair(moveCageY, newPos.second)
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
                )
            )
        }
    }

    fun switchGhostMode(mode: GhostMode, actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        val oldMode = this.mode
        this.mode = mode
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
    }

    private fun decideNextDir(
        ghost: Ghost,
        reversed: Boolean,
        updateGhost: (ActorUpdateInfo) -> Unit
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
                                    getEuclideanDistanceBetween(newTileCandidate, targetPos)
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
                    updateGhost(
                        ActorUpdateInfo(
                            position = ghost.position,
                            tilePos = ghost.tilePos,
                            lastGoodTilePos = ghost.lastGoodTilePos,
                            direction = ghost.direction,
                            lastActiveDir = ghost.lastActiveDir,
                            nextDir = nextDir,
                        )
                    )
                }

                GhostMode.FLEEING -> {
                    var newDir: Directions
                    do {
                        newDir = Directions.entries.random()
                    } while (destination.allowedDir.contains(newDir).not()
                        || newDir == dir.getOppositeDir()
                    )

                    updateGhost(
                        ActorUpdateInfo(
                            position = ghost.position,
                            tilePos = ghost.tilePos,
                            lastGoodTilePos = ghost.lastGoodTilePos,
                            direction = ghost.direction,
                            lastActiveDir = ghost.lastActiveDir,
                            nextDir = nextDir,
                        )
                    )
                }

                else -> {}
            }
        }

    }

    abstract fun updateTargetPos(pos: Pair<Float, Float>)

    abstract fun getMovesInCage(): List<MoveInCage>

    override fun adjustOverShootOnEnteringTile(
        playFieldTile: Tile,
        updateActor: (ActorUpdateInfo) -> Unit
    ) {
    }

    override fun reverseOnEnteringTile(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        if (this.reverseDirectionsNext) { // reverse its direction
            if (actor is Ghost) {
                val dir = actor.direction.getOppositeDir()
                val nextDir = Directions.NONE
                this.reverseDirectionsNext = false
                decideNextDir(actor, reversed = true, updateActor)
            }
        }
    }

    override fun handleObjectOnEncounter(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
    }

    override fun decideNextDirAfterEnteredTile(
        actor: Actor,
        updateActor: (ActorUpdateInfo) -> Unit
    ) {
        if (actor is Ghost) {
            decideNextDir(ghost = actor, updateGhost = updateActor, reversed = false)
        }
    }

    override fun haveFood(tilePos: Pair<Int, Int>) {
    }
}