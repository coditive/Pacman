package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.CurrentSpeed
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.toBlinky
import com.syrous.pacman.util.LEAVING_SPEED
import com.syrous.pacman.util.UnitScale
import com.syrous.pacman.util.ghostSpeed
import com.syrous.pacman.util.ghostTunnelSpeed
import kotlinx.coroutines.flow.MutableStateFlow

class BlinkyController(private val gameState: GameState) : GhostController(gameState) {

    val ghost: MutableStateFlow<Blinky> = MutableStateFlow(
        Blinky(
            position = Pair(15 * UnitScale.toFloat(), 12 * UnitScale.toFloat()),
            tilePos = Pair(15, 12),
            lastGoodTilePos = Pair(15, 12),
            screenPos = Pair(15f * UnitScale, 12f * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
            physicalSpeed = 0f,
            fullSpeed = ghostSpeed,
            tunnelSpeed = ghostTunnelSpeed,
            speed = CurrentSpeed.NORMAL
        )
    )

    private val MOVES_IN_CAGE = mutableMapOf<GhostMode, List<MoveInCage>>().apply {
        put(
            GhostMode.RE_LEAVING_CAGE, listOf(
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
        scatterPos = Pair(1 * UnitScale.toFloat(), 1 * UnitScale.toFloat())
        actor = Blinky(
            position = Pair(15 * UnitScale.toFloat(), 12 * UnitScale.toFloat()),
            tilePos = Pair(15, 12),
            lastGoodTilePos = Pair(15, 12),
            screenPos = Pair(15f * UnitScale, 12f * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
            physicalSpeed = 0f,
            fullSpeed = ghostSpeed,
            tunnelSpeed = ghostTunnelSpeed,
            speed = CurrentSpeed.NORMAL
        )
        ghost.value = Blinky(
            position = Pair(15 * UnitScale.toFloat(), 12 * UnitScale.toFloat()),
            tilePos = Pair(15, 12),
            lastGoodTilePos = Pair(15, 12),
            screenPos = Pair(15f * UnitScale, 12f * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
            physicalSpeed = 0f,
            fullSpeed = ghostSpeed,
            tunnelSpeed = ghostTunnelSpeed,
            speed = CurrentSpeed.NORMAL
        )
    }

    override fun getNormalSpeed(): Float {
        return if (mode == GhostMode.PATROLLING || mode == GhostMode.CHASING) gameState.getCruiseElroySpeed()
        else actor.fullSpeed
    }

    override fun updateTargetPos() {
        val pacman = gameState.pacman.value
        when {
            mode == GhostMode.CHASING -> {
                targetPos = Pair(
                    pacman.tilePos.first * UnitScale.toFloat(),
                    pacman.tilePos.second * UnitScale.toFloat()
                )
            }
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
                } else {
                    step()
                    if (mode == GhostMode.EATEN) {
                        step()
                    }
                }
            }
        }
    }

    override fun updateActor(actorUpdateInfo: ActorUpdateInfo) {
        ghost.value = actorUpdateInfo.toBlinky(
            scaleFactorX,
            scaleFactorY,
        )
        actor = actorUpdateInfo.toBlinky(
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