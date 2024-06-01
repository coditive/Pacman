package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.CurrentSpeed
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Pinky
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.toPinky
import com.syrous.pacman.util.IN_CAGE_SPEED
import com.syrous.pacman.util.LEAVING_SPEED
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.ghostSpeed
import com.syrous.pacman.util.ghostTunnelSpeed
import com.syrous.pacman.util.minus
import com.syrous.pacman.util.plus
import kotlinx.coroutines.flow.MutableStateFlow

class PinkyController(private val gameState: GameState) : GhostController(gameState) {

    val ghost: MutableStateFlow<Pinky> = MutableStateFlow(
        Pinky(
            position = Pair(0f, 0f),
            tilePos = Pair(0, 0),
            lastGoodTilePos = Pair(0, 0),
            screenPos = Pair(0f, 0f),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT,
            physicalSpeed = 0f,
            fullSpeed = 0f,
            tunnelSpeed = 0f,
            speed = CurrentSpeed.NORMAL
        )
    )

    private val MOVES_IN_CAGE = mutableMapOf<GhostMode, List<MoveInCage>>().apply {
        put(
            GhostMode.RE_LEAVING_CAGE, listOf(
                MoveInCage(14.5f, 15f, Directions.UP, 3f, LEAVING_SPEED),
                MoveInCage(14.5f, 12f, Directions.LEFT, 0.5f, LEAVING_SPEED)
            )
        )
        put(
            GhostMode.IN_CAGE, listOf(
                MoveInCage(14f, 15f, Directions.RIGHT, 16f, IN_CAGE_SPEED),
                MoveInCage(16f, 15f, Directions.LEFT, 13f, IN_CAGE_SPEED),
                MoveInCage(13f, 15f, Directions.RIGHT, 14f, IN_CAGE_SPEED)
            )
        )
        put(
            GhostMode.LEAVING_CAGE, listOf(
                MoveInCage(14.5f, 15f, Directions.UP, 12f, LEAVING_SPEED),
                MoveInCage(14.5f, 12f, Directions.LEFT, 14f, LEAVING_SPEED),
            )
        )
        put(
            GhostMode.ENTERING_CAGE, listOf(
                MoveInCage(14f, 12f, Directions.RIGHT, 14.5f, 0.8f * 0.6f),
                MoveInCage(14.5f, 12f, Directions.DOWN, 15f, 0.8f * 0.6f)
            )
        )
    }

    override fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int) {
        this.playField = playField
        this.scaleFactorX = scaleFactorX
        this.scaleFactorY = scaleFactorY
        scatterPos = Pair(1 * scaleFactorX.toFloat(), 27 * scaleFactorY.toFloat())
        actor = Pinky(
            position = Pair(13 * UnitScale.toFloat(), 15 * UnitScale.toFloat()),
            tilePos = Pair(13, 15),
            lastGoodTilePos = Pair(13, 15),
            screenPos = Pair(13f * scaleFactorX * UnitScale, 15f * scaleFactorY * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
            physicalSpeed = 0f,
            fullSpeed = ghostSpeed,
            tunnelSpeed = ghostTunnelSpeed,
            speed = CurrentSpeed.NORMAL
        )
        ghost.value = Pinky(
            position = Pair(13 * UnitScale.toFloat(), 15 * UnitScale.toFloat()),
            tilePos = Pair(13, 15),
            lastGoodTilePos = Pair(13, 15),
            screenPos = Pair(13f * scaleFactorX * UnitScale, 15f * scaleFactorY * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
            physicalSpeed = 0f,
            fullSpeed = ghostSpeed,
            tunnelSpeed = ghostTunnelSpeed,
            speed = CurrentSpeed.NORMAL
        )
    }

    override fun detectCollision(targetPos: Pair<Int, Int>): Boolean {
        return targetPos == actor.tilePos
    }

    override fun updateTargetPos() {
        if (mode != GhostMode.CHASING) {
            return
        }
        val pacman = gameState.pacman.value
        targetPos = Pair(
            pacman.tilePos.first.toFloat() * UnitScale, pacman.tilePos.second.toFloat() * UnitScale
        ) + Pair(32 * pacman.direction.move.first, 32 * pacman.direction.move.second)
        if (pacman.direction == Directions.UP) {
            targetPos -= Pair(0f, 32f)
        }
    }

    override fun getMovesInCage(): List<MoveInCage> {
        return if (MOVES_IN_CAGE.containsKey(mode)) MOVES_IN_CAGE[mode]!!
        else emptyList()
    }

    override fun move() {
        if (gameState.getGamePlayMode() == GamePlayMode.ORDINARY_PLAYING || gameState.getGamePlayMode() == GamePlayMode.GHOST_DIED && (mode == GhostMode.EATEN || mode == GhostMode.ENTERING_CAGE)) {
            if (followingRoutine) {
                followRoutine()
                if (mode == GhostMode.ENTERING_CAGE) {
                    followRoutine()
                }
            } else {
                step()
                if (mode == GhostMode.EATEN) {
                    step()
                }
            }
        }
    }

    override fun updateActor(actorUpdateInfo: ActorUpdateInfo) {
        ghost.value = actorUpdateInfo.toPinky(
            scaleFactorX,
            scaleFactorY,
        )
        actor = actorUpdateInfo.toPinky(
            scaleFactorX,
            scaleFactorY,
        )
    }

    override fun changeCurrentSpeed(speed: CurrentSpeed) {
        actor = ghost.value.copy(speed = speed)
        changeCurrentSpeed()
    }

    override fun changeCurrentSpeed() {
        val s = when (actor.speed) {
            CurrentSpeed.NONE -> 0f
            CurrentSpeed.NORMAL -> getNormalSpeed()
            CurrentSpeed.PACMAN_EATING -> 0f
            CurrentSpeed.PASSING_TUNNEL -> actor.tunnelSpeed
        }
        if (actor.physicalSpeed != s) {
            actor = ghost.value.copy(physicalSpeed = s)
            intervalSpeedTable = gameState.getSpeedIntervals(s)
        }
    }

    override fun setReverseDirectionNext(reversed: Boolean) {
        reverseDirectionsNext = reversed
    }

}