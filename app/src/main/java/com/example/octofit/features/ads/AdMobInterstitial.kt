package com.example.octofit.features.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.time.Duration

@Stable
class InterstitialAdController internal constructor(
    private val context: Context,
    private val adUnitId: String,
    private val minShowInterval: Duration,
    private val minLoadInterval: Duration,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() },
) {
    var isAdReady by mutableStateOf(false)
        private set

    private var interstitialAd: InterstitialAd? = null
    private var lastShownAt: Long = 0L
    private var lastLoadAttemptAt: Long = 0L

    fun loadIfNeeded() {
        if (!AdMobConfig.isAdUnitIdConfigured(adUnitId)) {
            return
        }
        if (interstitialAd != null) {
            return
        }
        val now = clock()
        if (now - lastLoadAttemptAt < minLoadInterval.inWholeMilliseconds) {
            return
        }
        lastLoadAttemptAt = now
        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdReady = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isAdReady = false
                }
            },
        )
    }

    fun showIfReady(
        activity: Activity,
        onDismissed: () -> Unit = {},
        onFailedToShow: (AdError) -> Unit = {},
    ): Boolean {
        val ad = interstitialAd ?: return false
        val now = clock()
        if (now - lastShownAt < minShowInterval.inWholeMilliseconds) {
            return false
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                isAdReady = false
                onDismissed()
                loadIfNeeded()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                isAdReady = false
                onFailedToShow(error)
                loadIfNeeded()
            }
        }
        lastShownAt = now
        isAdReady = false
        interstitialAd = null
        ad.show(activity)
        return true
    }

    fun dispose() {
        interstitialAd = null
        isAdReady = false
    }
}

@Composable
fun rememberInterstitialAdController(
    adUnitId: String = AdMobConfig.interstitialAdUnitId,
    minShowInterval: Duration = AdMobConfig.minInterstitialInterval,
    minLoadInterval: Duration = AdMobConfig.minLoadInterval,
): InterstitialAdController {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember(adUnitId, minShowInterval, minLoadInterval) {
        InterstitialAdController(
            context = context,
            adUnitId = adUnitId,
            minShowInterval = minShowInterval,
            minLoadInterval = minLoadInterval,
        )
    }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controller.loadIfNeeded()
                Lifecycle.Event.ON_DESTROY -> controller.dispose()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        controller.loadIfNeeded()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.dispose()
        }
    }

    return controller
}
