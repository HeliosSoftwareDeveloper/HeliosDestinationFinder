package com.heliossoftwaredeveloper.examteramind.Route

import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import com.directions.route.Segment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.heliossoftwaredeveloper.examteramind.Constants.END_POINT_DESTINATION_LATITUDE
import com.heliossoftwaredeveloper.examteramind.Constants.END_POINT_DESTINATION_LONGITUDE
import com.heliossoftwaredeveloper.examteramind.Constants.LOCATION_DISTANCE_TOLERANCE
import java.util.ArrayList

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * Asynctask Class to process cache route
 */

class CacheRouteTask(private val lastKnownRouteSegment : ArrayList<Segment>, private val lastKnownRoutePoints : ArrayList<LatLng>, private val location : Location, private val callback : CacheRouteTaskListener) :
        AsyncTask<Void, Void, CacheRouteTask.CacheRouteTaskReturnType>() {

    private val endDestination = LatLng(END_POINT_DESTINATION_LATITUDE, END_POINT_DESTINATION_LONGITUDE)

    override fun doInBackground(vararg params: Void?): CacheRouteTaskReturnType? {
        var listRouteSegment = ArrayList<Segment>()
        val segmentSize = lastKnownRouteSegment.size

        val listNextPathLocation = ArrayList<LatLng>()
        listNextPathLocation.add(LatLng(location.latitude, location.longitude))
        for (polyline in lastKnownRoutePoints) {
            val locationCurrentPolyline = Location(LocationManager.GPS_PROVIDER)
            locationCurrentPolyline.latitude = polyline.latitude
            locationCurrentPolyline.longitude = polyline.longitude

            if (location.distanceTo(locationCurrentPolyline) > 30){
                listNextPathLocation.add(polyline)
            }
        }

        lastKnownRouteSegment.forEachIndexed { index, element ->
            val nextElementIndex = (index + 1)

            var listRoutePoints = ArrayList<LatLng>()

            if (nextElementIndex < segmentSize){
                listRoutePoints.add(element.startPoint())
                listRoutePoints.add(lastKnownRouteSegment[nextElementIndex].startPoint())

                if (PolyUtil.isLocationOnPath(LatLng(location.latitude, location.longitude), listRoutePoints, true, LOCATION_DISTANCE_TOLERANCE)){
                    listRouteSegment.add(element)
                }
            } else if (nextElementIndex == segmentSize) {
                listRoutePoints.add(element.startPoint())
                listRoutePoints.add(endDestination)

                if (listRouteSegment.isNotEmpty()) {
                    listRouteSegment.add(element)
                } else {
                    if (PolyUtil.isLocationOnPath(LatLng(location.latitude, location.longitude), listRoutePoints, true, LOCATION_DISTANCE_TOLERANCE)){
                        listRouteSegment.add(element)
                    }
                }
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