package com.example.octofit.features.ads

import androidx.compose.runtime.Immutable
import com.example.octofit.BuildConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Immutable
object AdMobConfig {
    val bannerAdUnitId: String = BuildConfig.ADMOB_BANNER_AD_UNIT_ID
    val interstitialAdUnitId: String = BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID

    val minInterstitialInterval: Duration = 2.minutes
    val minLoadInterval: Duration = 30.seconds

    fun isAdUnitIdConfigured(adUnitId: String): Boolean = adUnitId.isNotBlank()
}
