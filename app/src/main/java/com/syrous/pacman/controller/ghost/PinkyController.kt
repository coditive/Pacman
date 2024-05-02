package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Pinky
import com.syrous.pacman.model.Tile
import com.syrous.pacman.model.toPinky
import kotlinx.coroutines.flow.MutableStateFlow

class PinkyController(gameState: GameState) : GhostController(gameState) {

    val ghost: MutableStateFlow<Pinky> = MutableStateFlow(
        Pinky(
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
                MoveInCage(14.5f, 15f, Directions.UP, 3f, 1.6f),
                MoveInCage(14.5f, 12f, Directions.LEFT, 0.5f, 1.6f)
            )
        )
        put(GhostMode.IN_CAGE, listOf(MoveInCage(14.5f, 13f, Directions.DOWN, 3f, 0.8f * 0.6f)))
        put(
            GhostMode.LEAVING_CAGE, listOf(
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
        scatterPos = Pair(1 * scaleFactorX.toFloat(), 27 * scaleFactorY.toFloat())
    }

    override fun updateTargetPos(pos: Pair<Float, Float>) {
        TODO("Not yet implemented")
    }

    override fun getMovesInCage(): List<MoveInCage> {
        return if (MOVES_IN_CAGE.containsKey(mode))
            MOVES_IN_CAGE[mode]!!
        else emptyList()
    }

    override fun move() {
        if (mode == GhostMode.EATEN || mode == GhostMode.ENTERING_CAGE) {
            if (followingRoutine) {
                followRoutine(ghost.value) { actorUpdateInfo ->
                    ghost.value = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                    actor = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                }
                if (mode == GhostMode.ENTERING_CAGE) {
                    followRoutine(ghost.value) { actorUpdateInfo ->
                        ghost.value = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                        actor = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                    }
                }
            } else {
                step(ghost.value) { actorUpdateInfo ->
                    ghost.value = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                    actor = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                }
                if (mode == GhostMode.EATEN) {
                    step(ghost.value) { actorUpdateInfo ->
                        ghost.value = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                        actor = actorUpdateInfo.toPinky(scaleFactorX, scaleFactorY)
                    }
                }
            }
        }
    }

    override fun setReverseDirectionNext(reversed: Boolean) {
        reverseDirectionsNext = reversed
    }

}