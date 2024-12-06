package com.fadlurahmanfdev.example.core.service

import android.util.Log
import com.fadlurahmanfdev.feature_location.core.service.GPSLocationReceiver

class GPSLocationServiceImpl : GPSLocationReceiver() {
    override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
        Log.d(this::class.java.simpleName, "on detect gps changes: $gpsLocationEnabled")
    }
}