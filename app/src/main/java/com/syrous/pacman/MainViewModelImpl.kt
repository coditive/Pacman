package com.syrous.pacman

import androidx.lifecycle.ViewModel
import com.syrous.pacman.navigation.GameScreen
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModelImpl : ViewModel(), GameViewModel, GameController {

    override val currentScreen: MutableStateFlow<GameScreen> =
        MutableStateFlow(GameScreen.START_SCREEN)


}