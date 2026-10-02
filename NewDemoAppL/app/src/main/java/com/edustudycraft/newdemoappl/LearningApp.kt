package com.edustudycraft.newdemoappl

import android.app.Application
import com.edustudycraft.newdemoappl.di.AppContainer

class LearningApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
