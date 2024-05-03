package com.syrous.pacman

import android.app.Application
import timber.log.Timber


class PacmanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, "Game_$$tag", message, t)
                }

                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format(
                        "%s:%s",
                        element.methodName,
                        super.createStackElementTag(element)
                    )
                }
            })
        }
    }
}