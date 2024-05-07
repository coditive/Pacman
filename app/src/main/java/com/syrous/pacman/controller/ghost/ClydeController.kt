package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class ClydeController(private val gameState: GameState) : GhostController(gameState) {

    val ghost: MutableStateFlow<Clyde> = MutableStateFlow(
        Clyde(
            position = Pair(0f, 0f),
            tilePos = Pair(0, 0),
            lastGoodTilePos = Pair(0, 0),
            screenPos = Pair(0f, 0f),
            lastActiveDir = Directions.RIGHT,
            direction = Directions.RIGHT,
            nextDir = Directions.RIGHT,
        )
    )

    private val MOVES_IN_CAGE = mutableMapOf<GhostMode, List<MoveInCage>>().apply {
        put(
            GhostMode.RE_LEAVING_CAGE, listOf(
                MoveInCage(14.5f, 12f, Directions.RIGHT, 0.5f, 0.8f * 0.6f),
                MoveInCage(14.5f, 12f, Directions.UP, 3f, 0.8f * 0.6f),
            )
        )
        put(
            GhostMode.IN_CAGE, listOf(
                MoveInCage(13f, 15f, Directions.RIGHT, 1f, 0.8f * 0.6f),
                MoveInCage(14f, 15f, Directions.RIGHT, 2f, 0.8f * 0.6f),
                MoveInCage(16f, 17f, Directions.LEFT, 3f, 0.8f * 0.6f),
            )
        )
        put(
            GhostMode.LEAVING_CAGE, listOf(
                MoveInCage(14.5f, 12f, Directions.RIGHT, 0.5f, 0.8f * 0.6f),
                MoveInCage(14.5f, 12f, Directions.UP, 3f, 0.8f * 0.6f),
            )
        )
        put(
            GhostMode.ENTERING_CAGE, listOf(
                MoveInCage(15f, 12f, Directions.LEFT, 0.5f, 0.8f * 0.6f),
                MoveInCage(14.5f, 12f, Directions.DOWN, 3f, 0.8f * 0.6f),
            )
        )
    }

    override fun init(playField: Map<Int, Map<Int, Tile>>, scaleFactorX: Int, scaleFactorY: Int) {
        this.playField = playField
        this.scaleFactorX = scaleFactorX
        this.scaleFactorY = scaleFactorY
        scatterPos = Pair(1 * scaleFactorX.toFloat(), 30 * scaleFactorY.toFloat())
//        actor = Clyde(
//            position = Pair(16 * UnitScale.toFloat(), 15 * UnitScale.toFloat()),
//            tilePos = Pair(16, 15),
//            lastGoodTilePos = Pair(16, 15),
//            screenPos = Pair(16f * UnitScale * scaleFactorX, 15f * UnitScale * scaleFactorY),
//            lastActiveDir = Directions.RIGHT,
//            direction = Directions.RIGHT,
//            nextDir = Directions.NONE,
//        )
//        ghost.value = Clyde(
//            position = Pair(16 * UnitScale.toFloat(), 15 * UnitScale.toFloat()),
//            tilePos = Pair(16, 15),
//            lastGoodTilePos = Pair(16, 15),
//            screenPos = Pair(16f * UnitScale * scaleFactorX, 15f * UnitScale * scaleFactorY),
//            lastActiveDir = Directions.RIGHT,
//            direction = Directions.RIGHT,
//            nextDir = Directions.NONE,
//        )
    }

    override fun updateTargetPos(pos: Pair<Float, Float>) {
        TODO("Not yet implemented")
    }

    override fun getMovesInCage(): List<MoveInCage> {
        Timber.d("MovesInCage -> ${MOVES_IN_CAGE[mode]}, mode -> $mode")
        return if (MOVES_IN_CAGE.containsKey(mode)) MOVES_IN_CAGE[mode]!!
        else emptyList()
    }

    override fun move() {
//        if (gameState.getGamePlayMode() == GamePlayMode.ORDINARY_PLAYING || gameState.getGamePlayMode() == GamePlayMode.GHOST_DIED && (mode == GhostMode.EATEN || mode == GhostMode.ENTERING_CAGE)) {
//            if (followingRoutine) {
//                followRoutine { actorUpdateInfo ->
//                    ghost.value = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                    actor = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                }
//                if (mode == GhostMode.ENTERING_CAGE) {
//                    followRoutine { actorUpdateInfo ->
//                        ghost.value = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                        actor = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                    }
//                }
//            } else {
//                step { actorUpdateInfo ->
//                    ghost.value = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                    actor = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                }
//                if (mode == GhostMode.EATEN) {
//                    step { actorUpdateInfo ->
//                        ghost.value = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                        actor = actorUpdateInfo.toClyde(scaleFactorX, scaleFactorY)
//                    }
//                }
//            }
//        }
    }

    override fun setReverseDirectionNext(reversed: Boolean) {
        reverseDirectionsNext = reversed
    }

}