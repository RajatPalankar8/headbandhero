package com.proto.headbandhero

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.proto.headbandhero.ui.screens.PlatformerGameScreen
import com.proto.headbandhero.ui.theme.HeadbandHeroTheme

class MainActivity : ComponentActivity() {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Enable transparent edge-to-edge system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        // Disable navigation bar contrast enforcement to prevent solid background scrims on API 29+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Initialize Firebase Analytics (tracks automatically with google-services.json)
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        } catch (e: Exception) {
            // Safe fallback
        }

        setContent {
            HeadbandHeroTheme {
                PlatformerGameScreen()
            }
        }
    }
}
