package com.syrous.pacman.model




sealed class GameEvent {
    data object GhostAtePacman : GameEvent()
}