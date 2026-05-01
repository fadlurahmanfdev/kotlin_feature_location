package com.fadlurahmanfdev.example.presentation

import android.location.Address
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.FeatureModel
import com.fadlurahmanfdev.locx.LocX
import com.fadlurahmanfdev.locx.core.exception.LocXException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var locX: LocX

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check Fine Location Permission",
            desc = "Whether precise location permission granted",
            enum = "CHECK_FINE_LOCATION_PERMISSION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check Coarse Location Permission",
            desc = "Whether location permission granted",
            enum = "CHECK_COARSE_LOCATION_PERMISSION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Precise Location Permission",
            desc = "Request Precise Location Permission",
            enum = "REQUEST_FINE_LOCATION_PERMISSION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Approximate Location Permission",
            desc = "Request Approximate Location Permission",
            enum = "REQUEST_COARSE_LOCATION_PERMISSION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Check GPS Location Enabled",
            desc = "Whether GPS Location Enabled",
            enum = "CHECK_GPS_LOCATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Request Location Service",
            desc = "Request Location Service",
            enum = "REQUEST_LOCATION_SERVICE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Current Location From Fused Location Provider Client",
            desc = "Best for Device that have Google Play Services",
            enum = "CURRENT_LOCATION_USING_FUSED_LOCATION_PROVIDER_CLIENT"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Last Known Location using Fused Location Provider Client",
            desc = "Best for Device that doesnt have Google Play Services (e.g., Huawei, China Devices)",
            enum = "LAST_KNOWN_LOCATION_USING_FUSED_LOCATION_PROVIDER_CLIENT"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Current Location using Location Manager",
            desc = "Best for Device that doesnt have Google Play Services (e.g., Huawei, China Devices)",
            enum = "CURRENT_LOCATION_USING_LOCATION_MANAGER"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Last Known Location using Location Manager",
            desc = "Best for Device that doesnt have Google Play Services (e.g., Huawei, China Devices)",
            enum = "LAST_KNOWN_LOCATION_USING_LOCATION_MANAGER"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Listen GPS Location",
            desc = "Listen GPS Location",
            enum = "LISTEN_GPS_LOCATION_SERVICE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Remove Listen GPS Location",
            desc = "Remove Listen GPS Location",
            enum = "REMOVE_LISTEN_GPS_LOCATION_SERVICE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Address",
            desc = "Get Address",
            enum = "GET_ADDRESS"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Addresses From Location Name Synchronous",
            desc = "Get Addresses From Location Name Synchronous",
            enum = "GET_ADDRESSES_FROM_LOCATION_NAME_SYNCHRONOUS"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Addresses From Location Name Asynchronous",
            desc = "Get Addresses From Location Name Asynchronous",
            enum = "GET_ADDRESSES_FROM_LOCATION_NAME_ASYNCHRONOUS"
        ),
    )

    private lateinit var rv: RecyclerView
    private lateinit var adapter: ListExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rv = findViewById<RecyclerView>(R.id.rv)
        locX = LocX(this)

        rv.setItemViewCacheSize(features.size)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        rv.adapter = adapter
    }

    val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.d(MainActivity::class.java.simpleName, "is location permission granted: $it")
        }

    private var locationRequestLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            Log.d(
                MainActivity::class.java.simpleName,
                "request gps location service result: ${it.resultCode}"
            )
        }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "CHECK_FINE_LOCATION_PERMISSION" -> {
                val isPermissionGranted = locX.isFineLocationPermissionGranted()
                Log.d(
                    this::class.java.simpleName,
                    "is fine location permission granted: $isPermissionGranted"
                )
            }

            "CHECK_COARSE_LOCATION_PERMISSION" -> {
                val isPermissionGranted = locX.isCoarseLocationPermissionGranted()
                Log.d(
                    this::class.java.simpleName,
                    "is coarse location permission granted: $isPermissionGranted"
                )
            }

            "REQUEST_FINE_LOCATION_PERMISSION" -> {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            "REQUEST_COARSE_LOCATION_PERMISSION" -> {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            "CHECK_GPS_LOCATION" -> {
                val isGpsLocationEnabled = locX.isGPSLocationEnabled()
                Log.d(this::class.java.simpleName, "is gps location enabled: $isGpsLocationEnabled")
            }

            "REQUEST_LOCATION_SERVICE" -> {
                val locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                        .build()

                val settingRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)
                    .build()

                locX.requestGPSLocationService(
                    this,
                    locationRequest = locationRequest,
                    settingRequest = settingRequest,
                    object : LocX.RequestGPSLocationServiceCallback {
                        override fun onLocationServiceEnabled(enabled: Boolean) {
                            Log.d(
                                this@MainActivity::class.java.simpleName,
                                "App-LocX-LOG %%% - GPS Location Service enabled: $enabled"
                            )
                        }

                        override fun onShouldShowPromptServiceDialog(intentSenderRequest: IntentSenderRequest) {
                            Log.d(
                                this@MainActivity::class.java.simpleName,
                                "App-LocX-LOG %%% - GPS Location Service not enabled yet, should show prompt service dialog"
                            )
                            locationRequestLauncher.launch(intentSenderRequest)
                        }

                        override fun onFailure(exception: Exception) {
                            Log.d(
                                this@MainActivity::class.java.simpleName,
                                "App-LocX-LOG %%% - on failure request gps location service"
                            )
                        }

                        override fun onComplete() {
                            Log.d(
                                this@MainActivity::class.java.simpleName,
                                "App-LocX-LOG %%% - on complete request GPS Location Service"
                            )
                        }
                    },
                )
            }

            "CURRENT_LOCATION_USING_FUSED_LOCATION_PROVIDER_CLIENT" -> {
                val cancellationToken = CancellationTokenSource().token

                val locationRequest = CurrentLocationRequest
                    .Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setDurationMillis(30000L)
                    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    .build()

                locX.getCurrentLocation(
                    cancellationToken = cancellationToken,
                    locationRequest = locationRequest,
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(
                            this::class.java.simpleName,
                            "success get current location: ${location.latitude} & ${location.longitude}"
                        )
                    } else {
                        Log.d(
                            this::class.java.simpleName,
                            "location missing"
                        )
                    }
                }.addOnFailureListener {
                    Log.e(this::class.java.simpleName, "failed get current location: ${it.message}")
                }.addOnCompleteListener {
                    Log.d(this::class.java.simpleName, "completed get current location")
                }
            }

            "LAST_KNOWN_LOCATION_USING_FUSED_LOCATION_PROVIDER_CLIENT" -> {
                locX.getLastKnownLocation().addOnSuccessListener {
                    Log.d(
                        this::class.java.simpleName,
                        "success get last known location: ${it?.latitude} & ${it?.longitude}"
                    )
                }.addOnFailureListener {
                    Log.e(
                        this::class.java.simpleName,
                        "failed get last known location: ${it.message}"
                    )
                }.addOnCompleteListener {
                    Log.d(this::class.java.simpleName, "completed get last known location")
                }
            }

            "CURRENT_LOCATION_USING_LOCATION_MANAGER" -> {
                val cancellationSignal = CancellationSignal()
                locX.getCurrentLocation(
                    provider = LocationManager.NETWORK_PROVIDER,
                    cancellationSignal = cancellationSignal,
                    executor = ContextCompat.getMainExecutor(this),
                    consumer = Consumer { location ->
                        Log.d(
                            this@MainActivity::class.java.simpleName,
                            "App-Locx-LOG %%% - successfully get location: ${location.latitude}, ${location.longitude}"
                        )
                    }
                )
            }

            "LAST_KNOWN_LOCATION_USING_LOCATION_MANAGER" -> {
                val location = locX.getLastKnownLocation(
                    provider = LocationManager.NETWORK_PROVIDER,
                )
                Log.d(
                    this@MainActivity::class.java.simpleName,
                    "App-Locx-LOG %%% - successfully get location: ${location?.latitude}, ${location?.longitude}"
                )
            }

            "LISTEN_GPS_LOCATION_SERVICE" -> {
                locX.addGPSLocationServiceListener(
                    activity = this,
                    object : LocX.GPSLocationServiceListener {
                        override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
                            Log.d(
                                this@MainActivity::class.java.simpleName,
                                "is gps location enabled: $gpsLocationEnabled"
                            )
                        }
                    })
            }

            "REMOVE_LISTEN_GPS_LOCATION_SERVICE" -> {
                locX.removeGPSLocationServiceListener()
            }

            "GET_ADDRESS" -> {
                val cancellationToken = CancellationTokenSource().token

                val locationRequest = CurrentLocationRequest
                    .Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setDurationMillis(30000L)
                    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    .build()

                locX.getCurrentLocation(
                    cancellationToken = cancellationToken,
                    locationRequest = locationRequest,
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        locX.getAddressesByCoordinate(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            maxResult = 1,
                            callback = object : LocX.RequestAddressCallback {
                                override fun onGetAddress(addresses: List<Address>) {
                                    Log.d(
                                        this::class.java.simpleName,
                                        "App-LocX-LOG %%% - total address found: ${addresses.size}"
                                    )
                                    addresses.forEach { address ->
                                        Log.d(
                                            this::class.java.simpleName,
                                            "App-LocX-LOG %%% - address: $address"
                                        )
                                    }
                                }

                                override fun onFailedGetAddress(exception: LocXException) {
                                    Log.e(
                                        this::class.java.simpleName,
                                        "App-LocX-LOG %%% - failed get address: $exception"
                                    )
                                }
                            }
                        )
                    } else {
                        Log.d(
                            this::class.java.simpleName,
                            "App-LocX-LOG %%% - failed to get location"
                        )
                    }
                }.addOnFailureListener {
                    Log.e(
                        this::class.java.simpleName,
                        "App-LocX-LOG %%% - failed get current location: ${it.message}"
                    )
                }.addOnCompleteListener {
                    Log.d(
                        this::class.java.simpleName,
                        "App-LocX-LOG %%% - completed get current location"
                    )
                }
            }

            "GET_ADDRESSES_FROM_LOCATION_NAME_SYNCHRONOUS" -> {
                val addresses = locX.getAddressesByLocationName("Jakarta", 1)
                Log.d(
                    this::class.java.simpleName,
                    "App-LocX-LOG %%% - Total Address Found: ${addresses?.size}"
                )
                addresses?.forEach { address ->
                    Log.d(
                        this::class.java.simpleName,
                        "App-LocX-LOG %%% - address: $address"
                    )
                }
            }

            "GET_ADDRESSES_FROM_LOCATION_NAME_ASYNCHRONOUS" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    locX.getAddressesByLocationName(
                        "Jakarta", 1,
                        object : LocX.GeoCodeListener {
                            override fun onGetAddress(addresses: List<Address>) {
                                Log.d(
                                    this::class.java.simpleName,
                                    "App-LocX-LOG %%% - Total Address Found: ${addresses.size}"
                                )
                                addresses.forEach { address ->
                                    Log.d(
                                        this::class.java.simpleName,
                                        "App-LocX-LOG %%% - address: $address"
                                    )
                                }
                            }

                        },
                    )
                }
            }
        }
    }
}