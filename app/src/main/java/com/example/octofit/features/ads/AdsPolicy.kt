package com.example.octofit.features.ads

/**
 * Ad policy safeguards for AdMob integration:
 * - Never block core functionality if ads fail to load or show.
 * - Only show interstitials in user-initiated flows (e.g., after a completed action).
 * - Enforce cooldowns between interstitials to avoid rapid repeat impressions.
 * - Avoid retry loops; loading is throttled via [AdMobConfig.minLoadInterval].
 * - Use test ad unit IDs in debug builds and real production IDs in release builds.
 */
object AdsPolicy
