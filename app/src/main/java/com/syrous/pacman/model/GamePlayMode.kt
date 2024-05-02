package com.syrous.pacman.model

enum class GamePlayMode {
    ORDINARY_PLAYING, GHOST_DIED, PLAYER_DYING, PLAYER_DIED,
    NEWGAME_STARTING, NEWGAME_STARTED, GAME_RESTARTING, GAME_RESTARTED,
    GAMEOVER, LEVEL_BEING_COMPLETED, LEVEL_COMPLETED,
    TRANSITION_INTO_NEXT_SCENE, CUTSCENE, KILL_SCREEN;
}