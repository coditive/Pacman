package com.syrous.pacman.controller.ghost

import com.syrous.pacman.GameState
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.GhostMode
import com.syrous.pacman.model.MoveInCage
import com.syrous.pacman.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow

class ClydeController(gameState: GameState) : GhostController(gameState) {

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
        put(GhostMode.IN_CAGE, listOf())
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
                    Clyde(
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
                if (mode == GhostMode.ENTERING_CAGE) {
                    followRoutine(ghost.value) { actorUpdateInfo ->
                        Clyde(
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
            } else {
                step(ghost.value) { actorUpdateInfo ->
                    Clyde(
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
                if (mode == GhostMode.EATEN) {
                    step(ghost.value) { actorUpdateInfo ->
                        Clyde(
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
            }
        }
    }

    override fun switchGhostMode(ghostMode: GhostMode) {
        switchGhostMode(ghostMode, ghost.value) {
                actorUpdateInfo ->
            ghost.value = Clyde(
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

    override fun setReverseDirectionNext(reversed: Boolean) {
        reverseDirectionsNext = reversed
    }

}