package com.fadlurahmanfdev.locx

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.util.Consumer
import com.fadlurahmanfdev.locx.core.constant.ErrorLocXConstant
import com.fadlurahmanfdev.locx.core.exception.LocXException
import com.fadlurahmanfdev.locx.core.service.LocXGPSLocationReceiver
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import java.util.Locale
import java.util.concurrent.Executor

class LocX(private val context: Context) {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    val geoCoder = Geocoder(context, Locale.getDefault())

    /**
     * Checks if the [android.Manifest.permission.ACCESS_FINE_LOCATION] permission is granted.
     * For more approximate location data, consider using [isCoarseLocationPermissionGranted]
     * @see isCoarseLocationPermissionGranted
     */
    fun isFineLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the [android.Manifest.permission.ACCESS_COARSE_LOCATION] permission is granted.
     * For more precise location data, consider using [isFineLocationPermissionGranted].
     * @see isFineLocationPermissionGranted
     */
    fun isCoarseLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if the GPS Location enabled.
     * use [LocXGPSLocationReceiver] OR [LocXGPSLocationReceiver] to listen any changes for GPS Location.
     * @see LocXGPSLocationReceiver
     */
    fun isGPSLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            LocationManagerCompat.isLocationEnabled(locationManager)
        }
    }

    /**
     * Request to turn on GPS Location Service.
     * @param activity current activity
     * @param locationRequest configuration for location
     * @param settingRequest configuration for location setting
     * @param callback callback to request GPS Location Service
     * @see isGPSLocationEnabled
     * @see LocXGPSLocationReceiver
     * @see LocXGPSLocationReceiver
     */
    fun requestGPSLocationService(
        activity: Activity,
        locationRequest: LocationRequest? = null,
        settingRequest: LocationSettingsRequest? = null,
        callback: RequestGPSLocationServiceCallback,
    ) {
        val locationRequestBuilder =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)

        val settingRequestBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest ?: locationRequestBuilder.build())
            .setAlwaysShow(true)

        val task =
            LocationServices.getSettingsClient(activity)
                .checkLocationSettings(settingRequest ?: settingRequestBuilder.build())
        task.addOnSuccessListener { response ->
            callback.onLocationServiceEnabled(enabled = (response.locationSettingsStates?.isLocationPresent == true) && (response.locationSettingsStates?.isLocationUsable == true))
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                callback.onShouldShowPromptServiceDialog(
                    IntentSenderRequest.Builder(exception.resolution).build()
                )
            } else {
                callback.onFailure(exception)
            }
        }.addOnCompleteListener { locationSettingResponse ->
            callback.onComplete()
        }
    }

    /**
     * Get the last known location of this device using [FusedLocationProviderClient]
     * see [getCurrentLocation] for real time location.
     * @see getCurrentLocation
     */
    fun getLastKnownLocation(): Task<Location?> {
        return fusedLocationProviderClient
            .lastLocation
    }

    /**
     * Get the last known location of this device using [LocationManager]
     * @param provider provider of the location, best using [LocationManager.NETWORK_PROVIDER]
     * if using [LocationManager.GPS_PROVIDER], there is a possibility that it doesn't success return value when the device indoor or other case
     * @see getCurrentLocation
     */
    fun getLastKnownLocation(
        provider: String = LocationManager.NETWORK_PROVIDER,
    ): Location? {
        return locationManager.getLastKnownLocation(provider)
    }

    /**
     * Get current location using [FusedLocationProviderClient]
     * @param cancellationToken token for canceling location request.
     * @param locationRequest parameter to handling location request configuration such as timeout, priority, etc.
     */
    fun getCurrentLocation(
        cancellationToken: CancellationToken = CancellationTokenSource().token,
        locationRequest: CurrentLocationRequest?,
    ): Task<Location> {
        val locationRequestBuilder = CurrentLocationRequest
            .Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setDurationMillis(30000L)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        return fusedLocationProviderClient
            .getCurrentLocation(
                locationRequest ?: locationRequestBuilder.build(),
                cancellationToken
            )
    }

    /**
     * Get current location using [LocationManager]
     * @param provider provider of searching a location. Best using [LocationManager.NETWORK_PROVIDER], [LocationManager.GPS_PROVIDER] is more accurate,
     * but there is possibility that location is not returning in specific case like (indoor, etc)
     * @param cancellationSignal cancellation signal to cancel request a location
     */
    fun getCurrentLocation(
        provider: String = LocationManager.NETWORK_PROVIDER,
        cancellationSignal: CancellationSignal = CancellationSignal(),
        executor: Executor = ContextCompat.getMainExecutor(context),
        consumer: Consumer<Location>
    ) {
        LocationManagerCompat.getCurrentLocation(
            locationManager,
            provider,
            cancellationSignal,
            executor,
            consumer,
        )
    }

    /**
     * Get address by given coordinate
     * @param [latitude] latitude of current location, fetched by using [getLastKnownLocation] or [getCurrentLocation]
     * @param [longitude] longitude of current location, fetched by using [getLastKnownLocation] or [getCurrentLocation]
     * @param [maxResult] max resylt of the address found
     * @see getCurrentLocation
     * @see getLastKnownLocation
     */
    fun getAddressesByCoordinate(
        latitude: Double,
        longitude: Double,
        maxResult: Int = 1,
        callback: RequestAddressCallback
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geoCoder.getFromLocation(latitude, longitude, maxResult) { addresses ->
                    callback.onGetAddress(addresses)
                }
            } else {
                val addresses =
                    geoCoder.getFromLocation(latitude, longitude, maxResult) ?: listOf()
                callback.onGetAddress(addresses)
            }
        } catch (e: Throwable) {
            callback.onFailedGetAddress(
                LocXException(
                    code = ErrorLocXConstant.GENERAL.code,
                    message = e.message ?: "-",
                )
            )
        }
    }

    fun getAddressesByLocationName(
        locationName: String,
        maxResult: Int = 1,
    ): List<Address>? {
        return geoCoder.getFromLocationName(
            locationName,
            maxResult,
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getAddressesByLocationName(
        locationName: String,
        maxResult: Int = 1,
        geoCodeListener: GeoCodeListener
    ) {
        return geoCoder.getFromLocationName(
            locationName,
            maxResult
        ) { p0 ->
            geoCodeListener.onGetAddress(p0)
        }
    }

    private var gpsLocationServiceListener: GPSLocationServiceListener? = null
    private var activity: Activity? = null
    private val gpsLocationReceiver = object : LocXGPSLocationReceiver() {
        override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
            gpsLocationServiceListener?.isGpsLocationEnabled(gpsLocationEnabled)
        }
    }

    /**
     * Register the listener of GPS Location Changes.
     * */
    fun addGPSLocationServiceListener(activity: Activity, listener: GPSLocationServiceListener) {
        this.gpsLocationServiceListener = listener
        this.activity = activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.activity!!.registerReceiver(
                gpsLocationReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity.registerReceiver(
                gpsLocationReceiver,
                IntentFilter("android.location.PROVIDERS_CHANGED")
            )
        }
        Log.i(this::class.java.simpleName, "Locx-LOG %%% - successfully register receiver")
    }


    fun removeGPSLocationServiceListener() {
        this.gpsLocationServiceListener = null
        activity!!.unregisterReceiver(gpsLocationReceiver)
        Log.i(this::class.java.simpleName, "Locx-LOG %%% - successfully unregister receiver")
    }

    interface GPSLocationServiceListener {
        fun isGpsLocationEnabled(gpsLocationEnabled: Boolean)
    }

    interface RequestGPSLocationServiceCallback {
        fun onLocationServiceEnabled(enabled: Boolean)
        fun onShouldShowPromptServiceDialog(intentSenderRequest: IntentSenderRequest)
        fun onFailure(exception: Exception)
        fun onComplete()
    }

    interface RequestAddressCallback {
        fun onGetAddress(addresses: List<Address>)
        fun onFailedGetAddress(exception: LocXException)
    }

    interface GeoCodeListener {
        fun onGetAddress(addresses: List<Address>)
    }
}