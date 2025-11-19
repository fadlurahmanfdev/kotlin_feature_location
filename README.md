# Overview

Library for simplify fetch coordinate position, geocoding, etc.

## Installation

```kotlin
implementation("com.fadlurahmanfdev.locx:x.y.z")
```

## Initialization

```kotlin
val locx = LocX(context)
```

## Key Feature

### Permission & Service

```kotlin
// check whether Fine Permission for precise location is granted
val isFinePermissionGranted = locX.isFineLocationPermissionGranted()

// check whether Coarse Permission for approximately location is granted
val isCoarsePermissionGranted = locX.isCoarseLocationPermissionGranted()

// check whether GPS Location is enabled
val isGpsLocationEnabled = locX.isGPSLocationEnabled()

// request to turn on GPS Location Service
private var locationRequestLauncher: ActivityResultLauncher<IntentSenderRequest> =
    registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

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
        override fun onLocationServiceEnabled(enabled: Boolean) {}

        override fun onShouldShowPromptServiceDialog(intentSenderRequest: IntentSenderRequest) {
            locationRequestLauncher.launch(intentSenderRequest)
        }

        override fun onFailure(exception: Exception) {}

        override fun onComplete() {}
    },
)
```

### Fetch Coordinate or Location

Fetch location with Fused Location Provider Client. Best for Android devices who have Google Play
Services

```kotlin
val cancellationToken = CancellationTokenSource().token

val locationRequest = CurrentLocationRequest
    .Builder()
    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
    .setDurationMillis(30000L)
    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
    .build()

locX.getCurrentLocation(
    cancellationToken = cancellationToken,
    locationRequest = locationRequest
).addOnSuccessListener { location ->
    // do something with location 
}
    .addOnFailureListener {}
    .addOnCompleteListener {}
```

Fetch location with Location Manager. Best for Huawei or China devices who doesn't have Google Play
Services

```kotlin
val cancellationSignal = CancellationSignal()
locX.getCurrentLocation(
    provider = LocationManager.NETWORK_PROVIDER,
    cancellationSignal = cancellationSignal,
    executor = ContextCompat.getMainExecutor(this),
    consumer = Consumer { location ->
    }
)
```

Fetch last known Location

```kotlin
// fetch last known location using Fused Location Provider Client
locX.getLastKnownLocation()
    .addOnSuccessListener { location ->

    }.addOnFailureListener {

    }.addOnCompleteListener {

    }

// fetch last known location using Location Manager
val location = locX.getLastKnownLocation(
    provider = LocationManager.NETWORK_PROVIDER,
)
```

#### GPS Location Service Listener

Listening if GPS Location Service is changed

```kotlin
locX.addGPSLocationServiceListener(
    activity = this,
    object : LocX.GPSLocationServiceListener {
        override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {

        }
    }
)
```

Remove GPS Location Service Listener

```kotlin
locX.removeGPSLocationServiceListener()
```

### Get Address

Geocoding from latitude and longitude into address (e.g., city, street, adminArea, subAdminArea, etc).

```kotlin
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
                    
                }

                override fun onFailedGetAddress(exception: LocXException) {
                
                }
            }
        )
    } else { 
        
    }
}.addOnFailureListener {
    
}.addOnCompleteListener {

}
```