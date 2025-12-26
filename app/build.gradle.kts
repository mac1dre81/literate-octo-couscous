plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.octofit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.octofit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "ADMOB_BANNER_AD_UNIT_ID",
                "\"ca-app-pub-3940256099942544/6300978111\"",
            )
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"ca-app-pub-3940256099942544/1033173712\"",
            )
            resValue(
                "string",
                "admob_app_id",
                "ca-app-pub-3940256099942544~3347511713",
            )
        }
        release {
            val admobBannerId = providers.gradleProperty("ADMOB_BANNER_AD_UNIT_ID").orNull.orEmpty()
            val admobInterstitialId = providers.gradleProperty("ADMOB_INTERSTITIAL_AD_UNIT_ID").orNull.orEmpty()
            val admobAppId = providers.gradleProperty("ADMOB_APP_ID").orNull.orEmpty()
            buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"$admobBannerId\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID", "\"$admobInterstitialId\"")
            resValue("string", "admob_app_id", admobAppId)
        }
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.exifinterface)
    implementation(libs.metadata.extractor)
    implementation(libs.play.services.ads)

    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    debugImplementation(libs.compose.ui.tooling)
}
