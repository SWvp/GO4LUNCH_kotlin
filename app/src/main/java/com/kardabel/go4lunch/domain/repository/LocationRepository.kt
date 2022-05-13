package com.kardabel.go4lunch.domain.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.kardabel.go4lunch.MainApplication

class LocationRepository {

    // CHANGE CONST TO ADAPT LOCATION REFRESH
    companion object {
        const val DEFAULT_UPDATE_INTERVAL = 5000
        const val FASTEST_UPDATE_INTERVAL = 2000
    }
    private val locationMutableLiveData = MutableLiveData<Location>()
    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationRequest() {
        if (callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    locationMutableLiveData.value = location
                }
            }
            LocationServices.getFusedLocationProviderClient(MainApplication.getApplication())
                .requestLocationUpdates(
                    LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(DEFAULT_UPDATE_INTERVAL.toLong())
                        .setFastestInterval(FASTEST_UPDATE_INTERVAL.toLong())
                        .setSmallestDisplacement(50f),
                    callback as LocationCallback,
                    Looper.getMainLooper()
                )
        }
    }

    fun StopLocationRequest() {
        if (callback != null) {
            LocationServices.getFusedLocationProviderClient(MainApplication.getApplication())
                .removeLocationUpdates(
                    callback!!)
            callback = null
        }
    }

    fun getLocationLiveData(): LiveData<Location> {
        return locationMutableLiveData
    }
}