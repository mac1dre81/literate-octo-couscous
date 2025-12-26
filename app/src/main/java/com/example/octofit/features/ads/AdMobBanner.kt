package com.example.octofit.features.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.bannerAdUnitId,
    adSize: AdSize = AdSize.BANNER,
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {},
) {
    if (!AdMobConfig.isAdUnitIdConfigured(adUnitId)) {
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnAdLoaded by rememberUpdatedState(onAdLoaded)
    val latestOnAdFailed by rememberUpdatedState(onAdFailed)

    val adViewInstance = remember(adUnitId, adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    latestOnAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    latestOnAdFailed(error)
                }
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { adViewInstance },
        update = { adView ->
            if (!adView.isLoading) {
                adView.loadAd(AdRequest.Builder().build())
            }
        },
    )

    DisposableEffect(adViewInstance, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adViewInstance.resume()
                Lifecycle.Event.ON_PAUSE -> adViewInstance.pause()
                Lifecycle.Event.ON_DESTROY -> adViewInstance.destroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adViewInstance.destroy()
        }
    }
}
