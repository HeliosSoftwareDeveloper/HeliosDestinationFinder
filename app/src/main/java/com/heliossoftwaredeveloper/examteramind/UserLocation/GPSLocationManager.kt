package com.heliossoftwaredeveloper.examteramind.UserLocation

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.heliossoftwaredeveloper.examteramind.Constants.GPS_UPDATE_INTERVAL
import com.heliossoftwaredeveloper.examteramind.Constants.GPS_UPDATE_MIN_DISTANCE

/**
 * Created by Ruel N. Grajo on 07/03/2019.
 *
 * GPS Location Manager class
 */

class GPSLocationManager(private val context: Context, private val callback : GPSLocationManagerCallback) : LocationListener {

    private var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @TargetApi(Build.VERSION_CODES.M)
    fun startFetchingLocationUpdates(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL, GPS_UPDATE_MIN_DISTANCE, this)
    }

    fun removeFetchingLocationUpdates() {
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        callback.onLocationChanged(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        //do something in the future
    }

    override fun onProviderEnabled(p0: String?) {
        //do something in the future
    }

    override fun onProviderDisabled(p0: String?) {
        //do something in the future
    }

    interface GPSLocationManagerCallback {
        fun onLocationChanged(location: Location)
    }
}