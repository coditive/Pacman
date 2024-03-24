package com.syrous.pacman

class PacmanState {


    private var screenWidth = 0
    private var screenHeight = 0


    fun updateScreenDimensions(width: Int, height: Int) {
        if(width != screenWidth && height != screenHeight) {
            screenWidth = width
            screenHeight = height
        }
    }

}