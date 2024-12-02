package com.fadlurahmanfdev.example.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class ExampleCorePlatformUseCaseImpl(
) : ExampleCorePlatformUseCase {
//    override fun isLocationPermissionGranted(context: Context): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun isLocationEnabled(context: Context): Boolean {
//        return isLocationPermissionGranted(context) && platformRepository.isLocationServiceEnabled()
//    }

//    override fun getCurrentLocation(): Observable<CoordinateModel> {
//        return platformRepository.getCurrentCoordinate()
//    }
//
//
//    override fun getAddress(): Observable<AddressModel> {
//        return getCurrentLocation().flatMap { coordinate ->
//            platformRepository.getAddressByCoordinate(
//                latitude = coordinate.latitude,
//                longitude = coordinate.longitude
//            )
//        }
//    }


}