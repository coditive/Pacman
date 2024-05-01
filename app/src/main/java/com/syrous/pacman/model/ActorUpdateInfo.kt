package com.syrous.pacman.model

data class ActorUpdateInfo(
    val position: Pair<Float, Float>,
    val tilePos: Pair<Int, Int>,
    val lastGoodTilePos: Pair<Int, Int>,
    val lastActiveDir: Directions,
    val direction: Directions,
    val nextDir: Directions,
)


fun ActorUpdateInfo.toPacman(scaleFactorX: Int, scaleFactorY: Int): Pacman = Pacman(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir
)

