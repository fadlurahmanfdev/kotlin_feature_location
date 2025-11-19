package com.fadlurahmanfdev.example.core.service

import android.util.Log
import com.fadlurahmanfdev.locx.core.service.LocXGPSLocationReceiver

class LocXGPSLocationServiceImpl : LocXGPSLocationReceiver() {
    override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
        Log.d(this::class.java.simpleName, "on detect gps changes: $gpsLocationEnabled")
    }
}