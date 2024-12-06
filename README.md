# Overview

Library for simplified geo, location feature

## Key Feature

### GPS Receiver

#### GPS Location Receiver

Abstract class for later implementation every channel related to GPS Changes Detection.

```kotlin
class GPSLocationServiceImpl : GPSLocationReceiver() {
    override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
        Log.d(this::class.java.simpleName, "on detect gps changes: $gpsLocationEnabled")
    }
}
```

#### GPS Location Receiver Wrapper

GPS Location Receiver Wrapper Class for detect GPS Location Changes.

##### Initialize
```kotlin
val gpsLocationReceiverWrapper = GPSLocationReceiverWrapper(this)
```

##### Listening Changes
```kotlin
gpsLocationReceiverWrapper.addGPSChangeListener(object :
    GPSLocationReceiverWrapper.Listener {
    override fun isGpsLocationEnabled(gpsLocationEnabled: Boolean) {
        Log.d(
            this@LocationActivity::class.java.simpleName,
            "is gps location enabled: $gpsLocationEnabled"
        )
    }
})
```

##### Remove Listener
```kotlin
gpsLocationReceiverWrapper.removeGPSChangeListener()
```

### GPS Feature Location

#### Check Precise Location Permission Granted

```kotlin
val isPermissionGranted = featureLocation.isFineLocationPermissionGranted()
Log.d(this::class.java.simpleName, "is fine location permission granted: $isPermissionGranted")
```

#### Check Approximate Location Permission

```kotlin
val isPermissionGranted = featureLocation.isCoarseLocationPermissionGranted()
Log.d(
    this::class.java.simpleName,
    "is coarse location permission granted: $isPermissionGranted"
)
```

#### Check GPS Location Service Enabled

```kotlin
val isGpsLocationEnabled = featureLocation.isGPSLocationEnabled()
Log.d(this::class.java.simpleName, "is gps location enabled: $isGpsLocationEnabled")
```