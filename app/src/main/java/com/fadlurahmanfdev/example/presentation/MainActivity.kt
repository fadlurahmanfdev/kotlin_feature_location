package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.FeatureModel
import com.fadlurahmanfdev.feature_location.FeatureLocation
import com.fadlurahmanfdev.feature_location.core.exception.FeatureLocationException
import com.fadlurahmanfdev.feature_location.core.service.GPSLocationReceiver
import com.fadlurahmanfdev.feature_location.core.service.GPSLocationReceiverWrapper

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {

    lateinit var viewModel: MainViewModel
    lateinit var featureLocation: FeatureLocation
    lateinit var gpsLocationReceiverWrapper: GPSLocationReceiverWrapper

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
            title = "Get Last Known Location",
            desc = "Get Last Known Location",
            enum = "LAST_KNOWN_LOCATION"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Current Location",
            desc = "Get Current Location",
            enum = "CURRENT_LOCATION"
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
            title = "Get Coordinate",
            desc = "Get Coordinate Latitude Longitude",
            enum = "GET_COORDINATE"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Get Address",
            desc = "Get Address",
            enum = "GET_ADDRESS"
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
        featureLocation = FeatureLocation(this)
        gpsLocationReceiverWrapper = GPSLocationReceiverWrapper(this)

//        viewModel = MainViewModel(
//            exampleCorePlatformUseCase = ExampleCorePlatformUseCaseImpl(
//                platformRepository = CorePlatformLocationRepositoryImpl(
//                    applicationContext,
//                )
//            )
//        )

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter
    }

    val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.d(MainActivity::class.java.simpleName, "is location permission granted: $it")
        }

    private var locationRequestLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            Log.d(MainActivity::class.java.simpleName, "request gps location service result: ${it.resultCode}")
        }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "CHECK_FINE_LOCATION_PERMISSION" -> {
                val isPermissionGranted = featureLocation.isFineLocationPermissionGranted()
                Log.d(
                    this::class.java.simpleName,
                    "is fine location permission granted: $isPermissionGranted"
                )
            }

            "CHECK_COARSE_LOCATION_PERMISSION" -> {
                val isPermissionGranted = featureLocation.isCoarseLocationPermissionGranted()
                Log.d(
                    this::class.java.simpleName,
                    "is coarse location permission granted: $isPermissionGranted"
                )
            }

            "CHECK_COARSE_LOCATION" -> {
                val isPermissionGranted = featureLocation.isCoarseLocationPermissionGranted()
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
                val isGpsLocationEnabled = featureLocation.isGPSLocationEnabled()
                Log.d(this::class.java.simpleName, "is gps location enabled: $isGpsLocationEnabled")
            }

            "REQUEST_LOCATION_SERVICE" -> {
                featureLocation.requestGPSLocationService(
                    this,
                    object : FeatureLocation.RequestLocationServiceCallback {
                        override fun onLocationServiceEnabled(enabled: Boolean) {
                            Log.d(this@MainActivity::class.java.simpleName, "location service enabled: $enabled")
                        }

                        override fun onShouldShowPromptServiceDialog(intentSenderRequest: IntentSenderRequest) {
                            Log.d(this@MainActivity::class.java.simpleName, "should show prompt service dialog")
                            locationRequestLauncher.launch(intentSenderRequest)
                        }

                        override fun onFailure(exception: Exception) {
                            Log.d(this@MainActivity::class.java.simpleName, "on failure request gps location service")
                        }
                    },
                )
            }

            "LAST_KNOWN_LOCATION" -> {
                featureLocation.getLastKnownLocation().addOnSuccessListener {
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

            "CURRENT_LOCATION" -> {
                featureLocation.getCurrentLocation().addOnSuccessListener { location ->
                    if (location != null){
                        Log.d(
                            this::class.java.simpleName,
                            "success get current location: ${location.latitude} & ${location.longitude}"
                        )
                    }else{
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

            "LISTEN_GPS_LOCATION_SERVICE" -> {
                gpsLocationReceiverWrapper.addGPSChangeListener(object :
                    GPSLocationReceiverWrapper.Listener {
                    override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
                        Log.d(
                            this@MainActivity::class.java.simpleName,
                            "is gps location enabled: $gpsLocationEnabled"
                        )
                    }
                })
            }

            "REMOVE_LISTEN_GPS_LOCATION_SERVICE" -> {
                gpsLocationReceiverWrapper.removeGPSChangeListener()
            }

            "GET_COORDINATE" -> {
                viewModel.getCurrentLocation()
            }

            "GET_ADDRESS" -> {
                featureLocation.getAddresses(
                    callback = object : FeatureLocation.RequestAddressCallback {
                        override fun onGetAddress(addresses: List<Address>) {
                            Log.d(this::class.java.simpleName, "total address: ${addresses.size}")
                            addresses.forEach { address ->
                                Log.d(this::class.java.simpleName, "address: $address")
                            }
                        }

                        override fun onFailedGetLocation() {
                            Log.w(this@MainActivity::class.java.simpleName, "failed get location, need to request again")
                        }

                        override fun onFailedGetAddress(exception: FeatureLocationException) {
                            Log.e(this::class.java.simpleName, "failed get address: $exception")
                        }
                    }
                )
            }
        }
    }
}