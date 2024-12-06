package com.fadlurahmanfdev.feature_location.core.service

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log

/**
 * Helper class for implement & listen GPS Location Changed
 * */
class GPSLocationReceiverWrapper(private val activity: Activity) {
    private var listener: Listener? = null
    private val receiver = object : GPSLocationReceiver() {
        override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
            listener?.isGpsLocationEnabled(gpsLocationEnabled)
        }
    }

    /**
     * Register the listener of GPS Location Changes.
     * */
    fun addGPSChangeListener(listener: Listener) {
        this.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(
                receiver,
                IntentFilter("android.location.PROVIDERS_CHANGED"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity.registerReceiver(receiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
        }
        Log.i(this::class.java.simpleName, "successfully register receiver ${this::class.java.simpleName}")
    }


    fun removeGPSChangeListener() {
        this.listener = null
        activity.unregisterReceiver(receiver)
        Log.i(this::class.java.simpleName, "successfully unregister receiver ${this::class.java.simpleName}")
    }

    interface Listener {
        fun isGpsLocationEnabled(gpsLocationEnabled: Boolean)
    }
}