package com.fadlurahmanfdev.feature_location

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.fadlurahmanfdev.feature_location.core.constant.ErrorFeatureLocationConstant
import com.fadlurahmanfdev.feature_location.core.exception.FeatureLocationException
import com.fadlurahmanfdev.feature_location.core.service.GPSLocationReceiver
import com.fadlurahmanfdev.feature_location.core.service.GPSLocationReceiverWrapper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import java.util.Locale

class FeatureLocation(private val context: Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

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
     * use [GPSLocationReceiver] OR [GPSLocationReceiverWrapper] to listen GPS Location Changed.
     * @see GPSLocationReceiver
     * @see GPSLocationReceiverWrapper
     */
    fun isGPSLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            LocationManagerCompat.isLocationEnabled(locationManager)
        }
    }

    /**
     * Checks if the GPS Location enabled.
     * use [GPSLocationReceiver] OR [GPSLocationReceiverWrapper] to listen GPS Location Changed.
     * @param activity current activity
     * @param callback callback to request GPS Location Service
     * @see GPSLocationReceiver
     * @see GPSLocationReceiverWrapper
     */
    fun requestGPSLocationService(
        activity: Activity,
        callback: RequestLocationServiceCallback,
    ) {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                .build()

        val settingRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val task =
            LocationServices.getSettingsClient(activity)
                .checkLocationSettings(settingRequest.build())
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
            Log.d(this::class.java.simpleName, "on complete request location service")
        }
    }

    /**
     * Get the last known location of this device
     * see [getCurrentLocation] for real time location.
     * @see getCurrentLocation
     */
    fun getLastKnownLocation(): Task<Location?> {
        return getLastKnownLocation(fusedLocationProviderClient)
    }

    fun getLastKnownLocation(activity: Activity): Task<Location?> {
        val client = LocationServices.getFusedLocationProviderClient(activity)
        return getLastKnownLocation(client)
    }

    private fun getLastKnownLocation(client: FusedLocationProviderClient): Task<Location?> {
        val taskCompletionSource = TaskCompletionSource<Location?>()
        client.lastLocation.addOnSuccessListener { location ->
            taskCompletionSource.setResult(location)
        }.addOnFailureListener { exception ->
            taskCompletionSource.setException(exception)
        }
        return taskCompletionSource.task
    }

    /**
     * Get current location
     * see [getLastKnownLocation] for last known location.
     * @see getLastKnownLocation
     */
    fun getCurrentLocation(
        priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY,
    ): Task<Location> {
        return getCurrentLocation(fusedLocationProviderClient, priority = priority)
    }

    fun getCurrentLocation(
        activity: Activity,
        priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY,
    ): Task<Location> {
        val client: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
        return getCurrentLocation(client, priority = priority)
    }

    private fun getCurrentLocation(
        client: FusedLocationProviderClient,
        durationMillis: Long = 60000,
        priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY,
    ): Task<Location> {
        try {
            val bestProvider = locationManager.getBestProvider(Criteria(), true)
            Log.d(this@FeatureLocation::class.java.simpleName, "best provider: $bestProvider")
            locationManager.requestLocationUpdates(
                bestProvider!!,
                5000,
                10f
            ) { _ ->
                Log.d(this@FeatureLocation::class.java.simpleName, "success update location")
            }
        } catch (e: Throwable) {
            Log.w(this::class.java.simpleName, "failed request location update: ${e.message}")
        }

        val taskCompletionSource = TaskCompletionSource<Location>()
        client.getCurrentLocation(
            CurrentLocationRequest.Builder()
                .setDurationMillis(durationMillis)
                .setPriority(priority)
                .build(), null
        ).addOnSuccessListener { location ->
            taskCompletionSource.setResult(location)
        }
        return taskCompletionSource.task
    }

    /**
     * Get address by get current location first
     * see [getAddressesByCoordinate] for get address directly by custom coordinate
     * @see getAddressesByCoordinate
     */
    fun getAddresses(
        maxResult: Int = 1,
        callback: RequestAddressCallback
    ) {
        getCurrentLocation()
            .addOnFailureListener {
                callback.onFailedGetAddress(
                    FeatureLocationException(
                        code = ErrorFeatureLocationConstant.GENERAL.code,
                        message = it.message ?: "-",
                    )
                )
            }
            .addOnSuccessListener { location ->
                if (location != null) {
                    getAddressesByCoordinate(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        maxResult = maxResult,
                        callback = callback
                    )
                } else {
                    callback.onFailedGetLocation()
                }
            }
            .addOnCompleteListener { _ -> }
    }

    /**
     * Get address by given coordinate
     * see [getAddresses] for current coordinate without specify given coordinate
     * @see getAddresses
     */
    fun getAddressesByCoordinate(
        latitude: Double,
        longitude: Double,
        maxResult: Int = 1,
        callback: RequestAddressCallback
    ) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
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
                FeatureLocationException(
                    code = ErrorFeatureLocationConstant.GENERAL.code,
                    message = e.message ?: "-",
                )
            )
        }
    }

    interface RequestLocationServiceCallback {
        fun onLocationServiceEnabled(enabled: Boolean)
        fun onShouldShowPromptServiceDialog(intentSenderRequest: IntentSenderRequest)
        fun onFailure(exception: Exception)
    }

    interface RequestAddressCallback {
        fun onGetAddress(addresses: List<Address>)
        fun onFailedGetLocation()
        fun onFailedGetAddress(exception: FeatureLocationException)
    }
}