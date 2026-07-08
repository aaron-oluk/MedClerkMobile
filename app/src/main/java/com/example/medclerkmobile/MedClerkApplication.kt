package com.example.medclerkmobile

import android.app.Application
import com.example.medclerkmobile.data.AppContainer

class MedClerkApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
