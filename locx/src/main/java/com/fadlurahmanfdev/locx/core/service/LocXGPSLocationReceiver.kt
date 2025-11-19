package com.fadlurahmanfdev.locx.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fadlurahmanfdev.locx.LocX

/**
 * Class for listening whether GOS Location is changed
 * */
abstract class LocXGPSLocationReceiver(private val location: LocX? = null) : BroadcastReceiver() {
    private var gpsLocationEnabled: Boolean? = null
    lateinit var locX: LocX

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        initFeatureLocation(context)
        if (intent?.action == "android.location.PROVIDERS_CHANGED") {
            val gpsLocationEnabled = locX.isGPSLocationEnabled()
            if (this.gpsLocationEnabled != gpsLocationEnabled) {
                this.gpsLocationEnabled = gpsLocationEnabled
                isGpsLocationEnabled(gpsLocationEnabled)
            }
        }
    }

    abstract fun isGpsLocationEnabled(gpsLocationEnabled: Boolean)

    private fun initFeatureLocation(context: Context) {
        if (::locX.isInitialized) {
            return
        }
        locX = location ?: LocX(context)
    }
}