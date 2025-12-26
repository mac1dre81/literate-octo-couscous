package com.example.octofit.features.ads

import android.app.Application
import android.util.Log
import com.example.octofit.R
import com.google.android.gms.ads.MobileAds

object AdMobInitializer {
    private const val TAG = "AdMobInitializer"

    fun initialize(application: Application) {
        val appId = application.getString(R.string.admob_app_id)
        if (appId.isBlank()) {
            Log.w(TAG, "AdMob app id missing; skipping SDK initialization.")
            return
        }
        MobileAds.initialize(application)
    }
}
