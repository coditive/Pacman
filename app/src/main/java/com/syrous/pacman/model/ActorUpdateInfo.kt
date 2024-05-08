package com.syrous.pacman.model

data class ActorUpdateInfo(
    val position: Pair<Float, Float>,
    val tilePos: Pair<Int, Int>,
    val lastGoodTilePos: Pair<Int, Int>,
    val speed: CurrentSpeed,
    val physicalSpeed: Float,
    val fullSpeed: Float,
    val tunnelSpeed: Float,
    val lastActiveDir: Directions,
    val direction: Directions,
    val nextDir: Directions,
)


fun ActorUpdateInfo.toPacman(
    scaleFactorX: Int, scaleFactorY: Int, dotEatingSpeed: Float
): Pacman = Pacman(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir,
    fullSpeed = this.fullSpeed,
    tunnelSpeed = this.tunnelSpeed,
    physicalSpeed = this.physicalSpeed,
    speed = this.speed,
    dotEatingSpeed = dotEatingSpeed
)

fun ActorUpdateInfo.toBlinky(
    scaleFactorX: Int,
    scaleFactorY: Int,
): Blinky = Blinky(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir,
    fullSpeed = this.fullSpeed,
    tunnelSpeed = this.tunnelSpeed,
    physicalSpeed = this.physicalSpeed,
    speed = this.speed,
)

fun ActorUpdateInfo.toPinky(
    scaleFactorX: Int,
    scaleFactorY: Int,
): Pinky = Pinky(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir,
    fullSpeed = this.fullSpeed,
    tunnelSpeed = this.tunnelSpeed,
    physicalSpeed = this.physicalSpeed,
    speed = this.speed,
)

fun ActorUpdateInfo.toInky(
    scaleFactorX: Int,
    scaleFactorY: Int,
): Inky = Inky(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir,
    fullSpeed = this.fullSpeed,
    tunnelSpeed = this.tunnelSpeed,
    physicalSpeed = this.physicalSpeed,
    speed = this.speed,
)

fun ActorUpdateInfo.toClyde(
    scaleFactorX: Int, scaleFactorY: Int
): Clyde = Clyde(
    position = this.position,
    tilePos = this.tilePos,
    screenPos = Pair(this.position.first * scaleFactorX, this.position.second * scaleFactorY),
    lastGoodTilePos = this.lastGoodTilePos,
    lastActiveDir = this.lastActiveDir,
    direction = this.direction,
    nextDir = this.nextDir,
    fullSpeed = this.fullSpeed,
    tunnelSpeed = this.tunnelSpeed,
    physicalSpeed = this.physicalSpeed,
    speed = this.speed,
)