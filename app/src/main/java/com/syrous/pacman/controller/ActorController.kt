package com.syrous.pacman.controller

import com.syrous.pacman.GameState
import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Blinky
import com.syrous.pacman.model.Clyde
import com.syrous.pacman.model.CurrentSpeed
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Inky
import com.syrous.pacman.model.Pacman
import com.syrous.pacman.model.Pinky
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.TUNNEL_POSITION
import com.syrous.pacman.util.UnitScale
import kotlin.math.floor
import kotlin.math.round

abstract class ActorController(private val gameState: GameState) {

    protected lateinit var playField: Map<Int, Map<Int, Tile>>
    protected lateinit var actor: Actor
    protected lateinit var intervalSpeedTable: BooleanArray
    abstract fun move()
    abstract fun adjustOverShootOnEnteringTile(playFieldTile: Tile, actor: Actor)
    abstract fun reverseOnEnteringTile(actor: Actor)
    abstract fun handleObjectOnEncounter(actor: Actor)
    abstract fun decideNextDirAfterEnteredTile(actor: Actor)
    abstract fun haveFood(tilePos: Pair<Int, Int>)
    abstract fun changeCurrentSpeed(speed: CurrentSpeed)
    abstract fun changeCurrentSpeed()

    fun getOppositeDirection(directions: Directions): Directions =
        when (directions) {
            Directions.LEFT -> Directions.RIGHT
            Directions.RIGHT -> Directions.LEFT
            Directions.UP -> Directions.DOWN
            Directions.DOWN -> Directions.UP
            Directions.NONE -> Directions.NONE
        }

    fun getPlayFieldTile(tile: Pair<Int, Int>): Tile =
        playField[tile.first * UnitScale]!![tile.second * UnitScale]!!

    private fun getPlayFieldTilePos(tile: Pair<Int, Int>): Pair<Int, Int> =
        Pair(tile.first * UnitScale, tile.second * UnitScale)

    fun step(updateActor: (ActorUpdateInfo) -> Unit) {
        if (actor.direction == Directions.NONE || intervalSpeedTable[gameState.getIntervalTime()].not()) {
            return
        }

        val pos = actor.position
        val dir = actor.direction
        val tilePos = actor.tilePos

        val pacX = pos.first + dir.move.first
        val pacY = pos.second + dir.move.second
        val newPos = Pair(pacX, pacY)

        updateActor(
            ActorUpdateInfo(
                position = newPos,
                tilePos = actor.tilePos,
                lastGoodTilePos = actor.lastGoodTilePos,
                lastActiveDir = actor.lastActiveDir,
                direction = actor.direction,
                nextDir = actor.nextDir,
                physicalSpeed = actor.physicalSpeed,
                speed = actor.speed,
                fullSpeed = actor.fullSpeed,
                tunnelSpeed = actor.tunnelSpeed

            )
        )

        val imaginaryX = pacX / UnitScale
        val imaginaryY = pacY / UnitScale
        val nextTile = Pair(
            round(imaginaryX).toInt(),
            round(imaginaryY).toInt()
        )
        val enteredTile = Pair(
            floor(imaginaryX) * UnitScale,
            floor(imaginaryY) * UnitScale
        )
        when {
            nextTile.first != tilePos.first || nextTile.second != tilePos.second -> {
                enteringTile(nextTile, updateActor)
            }

            enteredTile == newPos -> {
                enteredTile(updateActor)
            }
        }
    }

    private fun canHaveFood(playFieldTile: Tile): Boolean =
        playFieldTile.food != Food.NONE

    private fun enteringTile(
        tilePos: Pair<Int, Int>,
        updateActor: (ActorUpdateInfo) -> Unit
    ) {
        adjustOverShootOnEnteringTile(getPlayFieldTile(tilePos), actor)

        reverseOnEnteringTile(actor)

        if (canHaveFood(getPlayFieldTile(tilePos))) {
            haveFood(getPlayFieldTilePos(tilePos))
        }

        updateActor(
            ActorUpdateInfo(
                position = actor.position,
                tilePos = tilePos,
                lastGoodTilePos = tilePos,
                lastActiveDir = actor.lastActiveDir,
                direction = actor.direction,
                nextDir = actor.nextDir,
                physicalSpeed = actor.physicalSpeed,
                speed = actor.speed,
                fullSpeed = actor.fullSpeed,
                tunnelSpeed = actor.tunnelSpeed
            )
        )
    }

    private fun enteredTile(updateActor: (ActorUpdateInfo) -> Unit) {
        wrapWhenInTunnel()
        handleObjectOnEncounter(actor)
        decideNextDirAfterEnteredTile(actor)
        var actorUpdateInfo = ActorUpdateInfo(
            position = actor.position,
            tilePos = actor.tilePos,
            lastGoodTilePos = actor.lastGoodTilePos,
            lastActiveDir = actor.lastActiveDir,
            direction = actor.direction,
            nextDir = actor.nextDir,
            physicalSpeed = actor.physicalSpeed,
            speed = actor.speed,
            fullSpeed = actor.fullSpeed,
            tunnelSpeed = actor.tunnelSpeed
        )

        val playFieldTile = getPlayFieldTile(actorUpdateInfo.tilePos)
        if (playFieldTile.isIntersection) {
            if (actor.nextDir != Directions.NONE && playFieldTile.allowedDir.contains(actor.nextDir)) {
                if (actor.direction != Directions.NONE) {
                    actorUpdateInfo =
                        actorUpdateInfo.copy(lastActiveDir = actor.direction)
                }
                updateActor(
                    actorUpdateInfo.copy(
                        direction = actorUpdateInfo.nextDir,
                        nextDir = Directions.NONE
                    )
                )
            } else if (playFieldTile.allowedDir.contains(actor.direction).not()) {
                if (actor.direction != Directions.NONE) {
                    actorUpdateInfo =
                        actorUpdateInfo.copy(lastActiveDir = actorUpdateInfo.direction)
                }
                updateActor(
                    actorUpdateInfo.copy(direction = Directions.NONE, nextDir = Directions.NONE)
                )
            }
        }
    }

    private fun wrapWhenInTunnel() {
        if (actor.position == Pair(
                TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                TUNNEL_POSITION[0].second * UnitScale.toFloat()
            )
        ) {
            actor = when (actor) {
                is Blinky -> (actor as Blinky).copy(
                    position = Pair(
                        TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[1].second * UnitScale.toFloat()
                    )
                )

                is Clyde -> (actor as Clyde).copy(
                    position = Pair(
                        TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[1].second * UnitScale.toFloat()
                    )
                )
                is Inky -> (actor as Inky).copy(
                    position = Pair(
                        TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[1].second * UnitScale.toFloat()
                    )
                )
                is Pinky -> (actor as Pinky).copy(
                    position = Pair(
                        TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[1].second * UnitScale.toFloat()
                    )
                )
                is Pacman -> (actor as Pacman).copy(
                    position = Pair(
                        TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[1].second * UnitScale.toFloat()
                    )
                )
            }
        } else if (actor.position == Pair(
                TUNNEL_POSITION[1].first * UnitScale.toFloat(),
                TUNNEL_POSITION[1].second * UnitScale.toFloat()
            )
        ) {
            actor = when (actor) {
                is Blinky -> (actor as Blinky).copy(
                    position = Pair(
                        TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[0].second * UnitScale.toFloat()
                    )
                )

                is Clyde -> (actor as Clyde).copy(
                    position = Pair(
                        TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[0].second * UnitScale.toFloat()
                    )
                )
                is Inky -> (actor as Inky).copy(
                    position = Pair(
                        TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[0].second * UnitScale.toFloat()
                    )
                )
                is Pinky -> (actor as Pinky).copy(
                    position = Pair(
                        TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[0].second * UnitScale.toFloat()
                    )
                )
                is Pacman -> (actor as Pacman).copy(
                    position = Pair(
                        TUNNEL_POSITION[0].first * UnitScale.toFloat(),
                        TUNNEL_POSITION[0].second * UnitScale.toFloat()
                    )
                )
            }

        }
    }
}