package com.syrous.pacman

import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.flow.StateFlow

interface GameViewModel {

    val currentScreen: StateFlow<GameScreen>
}