package com.heliossoftwaredeveloper.examteramind.Route

import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.util.Log
import com.directions.route.Segment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.heliossoftwaredeveloper.examteramind.Constants.LOCATION_DISTANCE_TOLERANCE
import java.util.ArrayList

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * Asynctask Class to process cache route
 */

class CacheRouteTask(private val lastKnownRouteSegment : ArrayList<Segment>, private val lastKnownRoutePoints : ArrayList<LatLng>, private val location : Location, private val callback : CacheRouteTaskListener) :
        AsyncTask<Void, Void, CacheRouteTask.CacheRouteTaskReturnType>() {

    override fun doInBackground(vararg params: Void?): CacheRouteTaskReturnType? {
        var listRouteSegment = ArrayList<Segment>()

        val listNextPathLocation = ArrayList<LatLng>()
        listNextPathLocation.add(LatLng(location.latitude, location.longitude))
        for (polyline in lastKnownRoutePoints) {
            val locationCurrentPolyline = Location(LocationManager.GPS_PROVIDER)
            locationCurrentPolyline.latitude = polyline.latitude
            locationCurrentPolyline.longitude = polyline.longitude

            if (location.distanceTo(locationCurrentPolyline) > 35F){
                listNextPathLocation.add(polyline)
            }
        }
        lastKnownRouteSegment.forEach {
            if (PolyUtil.isLocationOnPath(LatLng(location.latitude, location.longitude), listNextPathLocation, true, LOCATION_DISTANCE_TOLERANCE)){
                listRouteSegment.add(it)
            }
        }
        return CacheRouteTaskReturnType(listRouteSegment, listNextPathLocation)
    }

    override fun onPostExecute(result : CacheRouteTaskReturnType?) {
        super.onPostExecute(result)
        callback.onCacheRouteTaskFinish(result!!)
    }

    interface CacheRouteTaskListener{
        fun onCacheRouteTaskFinish(result : CacheRouteTaskReturnType)
    }

    data class CacheRouteTaskReturnType (val listRouteSegment: ArrayList<Segment>, val listAllRoutePoints: ArrayList<LatLng>)
}