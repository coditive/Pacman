package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GamePlayMode
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.toBlinky
import com.syrous.pacman.util.UnitScale
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class BlinkyController(private val gameState: GameState) : GhostController(gameState) {

    val ghost: MutableStateFlow<Blinky> = MutableStateFlow(
        Blinky(
            position = Pair(0f, 0f),
            tilePos = Pair(0, 0),
            lastGoodTilePos = Pair(0, 0),
            screenPos = Pair(0f, 0f),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
        )
    )

    private val MOVES_IN_CAGE = mutableMapOf<GhostMode, List<MoveInCage>>().apply {
        put(
            GhostMode.RE_LEAVING_CAGE, listOf(
                MoveInCage(14.5f, 15f, Directions.UP, 3f, 1.6f),
                MoveInCage(14.5f, 12f, Directions.LEFT, 0.5f, 1.6f)
            )
        )
        put(
            GhostMode.ENTERING_CAGE, listOf(
                MoveInCage(14f, 12f, Directions.RIGHT, 0.5f, 0.8f * 0.6f),
                MoveInCage(14.5f, 12f, Directions.DOWN, 3f, 0.8f * 0.6f)
            )
        )
    }

    override fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int) {
        this.playField = playField
        this.scaleFactorX = scaleFactorX
        this.scaleFactorY = scaleFactorY
        scatterPos = Pair(1 * UnitScale.toFloat(), 1 * UnitScale.toFloat())
        Timber.d("scaleFactorX => $scaleFactorX, scaleFactorY => $scaleFactorY")
        actor = Blinky(
            position = Pair(15 * UnitScale.toFloat(), 12 * UnitScale.toFloat()),
            tilePos = Pair(15, 12),
            lastGoodTilePos = Pair(15, 12),
            screenPos = Pair(15f * UnitScale, 12f * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
        )
        ghost.value = Blinky(
            position = Pair(15 * UnitScale.toFloat(), 12 * UnitScale.toFloat()),
            tilePos = Pair(15, 12),
            lastGoodTilePos = Pair(15, 12),
            screenPos = Pair(15f * UnitScale, 12f * UnitScale),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.NONE,
        )
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
                followRoutine { actorUpdateInfo ->
                    ghost.value = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                    actor = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                }
                if (mode == GhostMode.ENTERING_CAGE) {
                    followRoutine { actorUpdateInfo ->
                        ghost.value = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                        actor = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                    }
                }
            } else {
                step { actorUpdateInfo ->
                    ghost.value = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                    actor = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                }
                if (mode == GhostMode.EATEN) {
                    step { actorUpdateInfo ->
                        ghost.value = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                        actor = actorUpdateInfo.toBlinky(scaleFactorX, scaleFactorY)
                    }
                }
            }
        }
    }

    override fun setReverseDirectionNext(reversed: Boolean) {
        reverseDirectionsNext = reversed
    }

}