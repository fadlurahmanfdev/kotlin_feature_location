package com.fadlurahmanfdev.feature_location.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fadlurahmanfdev.feature_location.FeatureLocation

abstract class GPSLocationReceiver(private val location: FeatureLocation? = null) : BroadcastReceiver() {
    private var gpsLocationEnabled: Boolean? = null
    lateinit var featureLocation: FeatureLocation

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        initFeatureLocation(context)
        if (intent?.action == "android.location.PROVIDERS_CHANGED") {
            val gpsLocationEnabled = featureLocation.isGPSLocationEnabled()
            if (this.gpsLocationEnabled != gpsLocationEnabled) {
                this.gpsLocationEnabled = gpsLocationEnabled
                isGpsLocationEnabled(gpsLocationEnabled)
            }
        }
    }

    abstract fun isGpsLocationEnabled(gpsLocationEnabled: Boolean)

    private fun initFeatureLocation(context: Context) {
        if (::featureLocation.isInitialized) {
            return
        }
        featureLocation = location ?: FeatureLocation(context)
    }
}