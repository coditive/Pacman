package com.syrous.pacman.controller

import com.syrous.pacman.model.Actor
import com.syrous.pacman.model.ActorUpdateInfo
import com.syrous.pacman.model.Directions
import com.syrous.pacman.model.Food
import com.syrous.pacman.model.Ghost
import com.syrous.pacman.model.Tile
import com.syrous.pacman.util.UnitScale
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.round

abstract class ActorController {

    protected lateinit var playField: Map<Int, Map<Int, Tile>>

    abstract fun move()
    abstract fun adjustOverShootOnEnteringTile(
        playFieldTile: Tile,
        updateActor: (ActorUpdateInfo) -> Unit
    )
    abstract fun reverseOnEnteringTile(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit)
    abstract fun handleObjectOnEncounter(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit)
    abstract fun decideNextDirAfterEnteredTile(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit)
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

    fun getPlayFieldTile(tile: Pair<Int, Int>): Tile =
        playField[tile.first * UnitScale]!![tile.second * UnitScale]!!

    fun getPlayFieldTilePos(tile: Pair<Int, Int>): Pair<Int, Int> =
        Pair(tile.first * UnitScale, tile.second * UnitScale)

    fun step(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        val pos = actor.position
        val dir = actor.direction
        val tilePos = actor.tilePos

        val pacX = pos.first + dir.move.first
        val pacY = pos.second + dir.move.second
        val newPos = Pair(pacX, pacY)
        Timber.d("before update actor -> newPos -> $newPos, dir -> $dir, tilePos -> $tilePos ")
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
        Timber.d("nextTile -> $nextTile, tilePos -> $tilePos,imaginaryX -> $imaginaryX, imaginaryY -> $imaginaryY,  newPos -> $newPos, enteredTile -> $enteredTile")
        when {
            nextTile.first != tilePos.first || nextTile.second != tilePos.second -> {
                enteringTile(actor, nextTile, updateActor)
            }

            enteredTile == newPos -> {
                enteredTile(actor, updateActor)
            }
        }
    }

    private fun canHaveFood(playFieldTile: Tile): Boolean =
        playFieldTile.food != Food.NONE

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
        reverseOnEnteringTile(actor) {
            Timber.d("reverseOnEnteringTile -> $it")
            actorUpdateInfo = it
        }

        if (canHaveFood(getPlayFieldTile(tilePos))) {
            haveFood(getPlayFieldTilePos(tilePos))
        }
        Timber.d("actorUpdateInfo -> $actorUpdateInfo")

        updateActor(actorUpdateInfo)
    }

    private fun enteredTile(actor: Actor, updateActor: (ActorUpdateInfo) -> Unit) {
        var actorUpdateInfo = ActorUpdateInfo(
            position = actor.position,
            tilePos = actor.tilePos,
            lastGoodTilePos = actor.lastGoodTilePos,
            lastActiveDir = actor.lastActiveDir,
            direction = actor.direction,
            nextDir = actor.nextDir,
        )
        Timber.d("newTile Entered!! and actor is ghost -> ${actor is Ghost}")

        handleObjectOnEncounter(actor) {
            actorUpdateInfo = it
        }
        decideNextDirAfterEnteredTile(actor) {
            actorUpdateInfo = it
        }

        val playFieldTile = getPlayFieldTile(actorUpdateInfo.tilePos)
        if (playFieldTile.isIntersection) {
            if (actorUpdateInfo.nextDir != Directions.NONE && playFieldTile.allowedDir.contains(actorUpdateInfo.nextDir)) {
                if (actorUpdateInfo.direction != Directions.NONE) {
                    actorUpdateInfo = actorUpdateInfo.copy(lastActiveDir = actorUpdateInfo.direction)
                }
                updateActor(actorUpdateInfo.copy(direction = actorUpdateInfo.nextDir, nextDir = Directions.NONE))
            } else if (playFieldTile.allowedDir.contains(actorUpdateInfo.direction).not()) {
                if (actorUpdateInfo.direction != Directions.NONE) {
                    actorUpdateInfo = actorUpdateInfo.copy(lastActiveDir = actorUpdateInfo.direction)
                }
                updateActor(
                    actorUpdateInfo.copy(direction = Directions.NONE, nextDir = Directions.NONE)
                )
            }
        }
    }
}