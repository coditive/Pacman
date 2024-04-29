package com.syrous.pacman

import android.app.Application
import timber.log.Timber


class PacmanApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}