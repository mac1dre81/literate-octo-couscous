package com.example.octofit

import android.app.Application
import com.example.octofit.features.ads.AdMobInitializer

class OctofitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AdMobInitializer.initialize(this)
    }
}
