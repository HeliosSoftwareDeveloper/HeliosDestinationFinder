package com.heliossoftwaredeveloper.examteramind.Route

import android.content.res.Resources
import android.location.Location
import android.os.AsyncTask
import com.directions.route.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.heliossoftwaredeveloper.examteramind.Constants
import com.heliossoftwaredeveloper.examteramind.Constants.LOCATION_DISTANCE_TOLERANCE
import com.heliossoftwaredeveloper.examteramind.R
import java.util.ArrayList

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * Route Manager Class
 */
class RouteManager(private val resources : Resources, private val mMap : GoogleMap, private val callback : RouteManagerCallback){

    private val endDestination = LatLng(Constants.END_POINT_DESTINATION_LATITUDE, Constants.END_POINT_DESTINATION_LONGITUDE)

    private var lastKnownPolyline : Polyline? = null
    private var lastKnownRoutePoints = ArrayList<LatLng>()
    private var lastKnownRoutePoints2 = ArrayList<LatLng>()
    private var lastKnownRouteSegment = ArrayList<Segment>()
    private var lastKnownDistance = 0
    private var lastKnownDuration = 0

    fun routeCurrentLocation(location : Location) {
        if (!PolyUtil.isLocationOnPath(LatLng(location.latitude, location.longitude), lastKnownRoutePoints, true, LOCATION_DISTANCE_TOLERANCE)) {
            routeFromAPIService(LatLng(location.latitude, location.longitude))
        } else {
            if (!PolyUtil.isLocationOnPath(LatLng(location.latitude, location.longitude), lastKnownRoutePoints2, true, LOCATION_DISTANCE_TOLERANCE)) {
                return
            }
            routeFromCache(location)
        }
    }

    private fun routeFromAPIService(startDestination : LatLng) {
        val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(object : RoutingListener {
                    override fun onRoutingCancelled() {}

                    override fun onRoutingStart() {}

                    override fun onRoutingFailure(exception: RouteException) {
                        callback.onRoutingFailed(exception.toString())
                    }

                    override fun onRoutingSuccess(listRoute: ArrayList<Route>, p1: Int) {
                        if (listRoute.isEmpty())
                            return

                        lastKnownRoutePoints.clear()
                        lastKnownRoutePoints2.clear()
                        lastKnownRouteSegment.clear()
                        clearPolylines()

                        lastKnownDistance = listRoute.first().distanceValue
                        lastKnownDuration = listRoute.first().durationValue
                        lastKnownRoutePoints.addAll(listRoute.first().points)
                        lastKnownRoutePoints2.addAll(listRoute.first().points)
                        lastKnownRouteSegment.addAll(listRoute.first().segments)
                        lastKnownPolyline = mMap.addPolyline(listRoute.first().polyOptions)
                        callback.onRoutingSuccess(listRoute)
                    }
                })
                .key(resources.getString(R.string.google_maps_key))
                .waypoints(startDestination, endDestination)
                .build()
        routing.execute()
    }

    private fun routeFromCache(location : Location) {
        val listRoutePoints = lastKnownRoutePoints.clone() as ArrayList<LatLng>
        val listRouteSegments = lastKnownRouteSegment.clone() as ArrayList<Segment>
        CacheRouteTask(listRouteSegments, listRoutePoints, location, object : CacheRouteTask.CacheRouteTaskListener {
            override fun onCacheRouteTaskFinish(result: CacheRouteTask.CacheRouteTaskReturnType) {
                lastKnownRouteSegment.clear()
                lastKnownRouteSegment.addAll(result?.listRouteSegment)
                if(lastKnownPolyline != null){
                    lastKnownPolyline!!.points = result?.listAllRoutePoints

                    lastKnownRoutePoints.clear()
                    lastKnownRoutePoints.addAll(result?.listAllRoutePoints)
                }

                callback.updateSegmentAdapter(result?.listRouteSegment!!)
                val duration = (lastKnownDuration / 100) * ((result?.distanceLeft * 100) / lastKnownDistance)
                callback.updateDurationDistanceLabel(result?.distanceLeft, duration)
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
    }

    private fun clearPolylines() {
        if(lastKnownPolyline != null)
            lastKnownPolyline!!.remove()
    }

    interface RouteManagerCallback {
        fun onRoutingSuccess(listRoute: ArrayList<Route>)
        fun onRoutingFailed(message : String)
        fun updateSegmentAdapter(listRouteSegment: ArrayList<Segment>)
        fun updateDurationDistanceLabel(distanceLeft : Float, durationLeft : Float)
    }

}