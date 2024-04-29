package com.syrous.pacman.controller

import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.UnitScale
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.round

abstract class ActorController(

) {

    lateinit var playField: Map<Int, Map<Int, Tile>>

    abstract fun move()
    abstract fun adjustOverShootOnEnteringTile(
        playFieldTile: Pair<Int, Int>,
        updateActor: (ActorUpdateInfo) -> Unit
    )

    abstract fun reverseOnEnteringTile()
    abstract fun handleObjectOnEncounter()
    abstract fun decideNextDirAfterEnteredTile()
    abstract fun haveFood(tilePos: Pair<Int, Int>)
    fun getOppositeDirection(directions: Directions): Directions {
        return when (directions) {
            Directions.LEFT -> Directions.RIGHT
            Directions.RIGHT -> Directions.LEFT
            Directions.UP -> Directions.DOWN
            Directions.DOWN -> Directions.UP
            Directions.NONE -> Directions.NONE
        }
    }

    fun getPlayFieldTile(tile: Pair<Int, Int>): Pair<Int, Int> =
        Pair(tile.first * UnitScale, tile.second * UnitScale)

    fun step(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        val pos = actor.position
        val dir = actor.direction
        val tilePos = actor.tilePos

        val pacX = pos.first + dir.move.first
        val pacY = pos.second + dir.move.second
        val newPos = Pair(pacX, pacY)

        Timber.d(
            "actorUpdateInfo -> ${
                ActorUpdateInfo(
                    position = newPos,
                    tilePos = actor.tilePos,
                    lastGoodTilePos = actor.lastGoodTilePos,
                    lastActiveDir = actor.lastActiveDir,
                    direction = actor.direction,
                    nextDir = actor.nextDir,
                )
            }"
        )
        updateActor(
            ActorUpdateInfo(
                position = newPos,
                tilePos = actor.tilePos,
                lastGoodTilePos = actor.lastGoodTilePos,
                lastActiveDir = actor.lastActiveDir,
                direction = actor.direction,
                nextDir = actor.nextDir,
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
                enteringTile(actor, nextTile, updateActor)
            }

            enteredTile == newPos -> {
                enteredTile(actor, updateActor)
            }
        }
    }

    private fun canHaveFood(playFieldTile: Pair<Int, Int>): Boolean =
        playField[playFieldTile.first]!![playFieldTile.second]!!.food != Food.NONE

    private fun enteringTile(
        actor: Actor,
        tilePos: Pair<Int, Int>,
        updateActor: (ActorUpdateInfo) -> Unit
    ) {
        var actorUpdateInfo = ActorUpdateInfo(
            position = actor.position,
            tilePos = tilePos,
            lastGoodTilePos = tilePos,
            lastActiveDir = actor.lastActiveDir,
            direction = actor.direction,
            nextDir = actor.nextDir,
        )
        adjustOverShootOnEnteringTile(getPlayFieldTile(tilePos)) {
            actorUpdateInfo = it
        }
        reverseOnEnteringTile()

        if (canHaveFood(getPlayFieldTile(tilePos))) {
            haveFood(getPlayFieldTile(tilePos))
        }
        Timber.d("entering Tile actorUpdateInfo -> $actorUpdateInfo")

        updateActor(actorUpdateInfo)
    }

    private fun enteredTile(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        val dir = actor.direction
        val nextDir = actor.nextDir
        val tilePos = actor.tilePos
        var lastActiveDir = actor.lastActiveDir
        val tile = getPlayFieldTile(tilePos)

        handleObjectOnEncounter()
        decideNextDirAfterEnteredTile()
        val playFieldTile = playField[tile.first]!![tile.second]!!
        Timber.d("entered Tile playFieldTile -> $playFieldTile")
        if (playFieldTile.isIntersection) {
            if (nextDir != Directions.NONE && playFieldTile.allowedDir.contains(nextDir)) {
                if (dir != Directions.NONE) {
                    lastActiveDir = dir
                }
                updateActor(
                    ActorUpdateInfo(
                        position = actor.position,
                        tilePos = actor.tilePos,
                        lastGoodTilePos = actor.lastGoodTilePos,
                        direction = nextDir,
                        nextDir = Directions.NONE,
                        lastActiveDir = lastActiveDir
                    )
                )
            } else if (playFieldTile.allowedDir.contains(dir).not()) {
                if (dir != Directions.NONE) {
                    lastActiveDir = dir
                }
                updateActor(
                    ActorUpdateInfo(
                        position = actor.position,
                        tilePos = actor.tilePos,
                        lastGoodTilePos = actor.lastGoodTilePos,
                        direction = Directions.NONE,
                        nextDir = Directions.NONE,
                        lastActiveDir = lastActiveDir
                    )
                )
            }
        }
    }
}