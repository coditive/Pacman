package com.syrous.pacman.model

sealed class GameInternalEvent {
    data class PacmanAteFood(val playFieldTile: Pair<Int, Int>,): GameInternalEvent()

}