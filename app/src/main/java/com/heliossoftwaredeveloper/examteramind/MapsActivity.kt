package com.heliossoftwaredeveloper.examteramind

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.directions.route.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.ArrayList
import android.location.Location
import android.os.Build
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.*
import com.heliossoftwaredeveloper.examteramind.Adapter.Decoration.DividerSpaceItemDecoration
import com.heliossoftwaredeveloper.examteramind.Adapter.SegmentListAdapter
import com.heliossoftwaredeveloper.examteramind.Constants.MAP_ZOOM_LEVEL
import com.heliossoftwaredeveloper.examteramind.Constants.REQUEST_CODE_USER_PERMISSION_ALL
import com.heliossoftwaredeveloper.examteramind.Route.RouteManager
import com.heliossoftwaredeveloper.examteramind.UserLocation.GPSLocationManager
import kotlinx.android.synthetic.main.view_loader.*
import kotlinx.android.synthetic.main.view_suggested_route.*

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * Main Activity class
 */

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, RouteManager.RouteManagerCallback, GPSLocationManager.GPSLocationManagerCallback {

    private lateinit var mMap: GoogleMap

    private val endDestination = LatLng(Constants.END_POINT_DESTINATION_LATITUDE, Constants.END_POINT_DESTINATION_LONGITUDE)

    private var segmentListAdapter : SegmentListAdapter? = null

    private var routeManager : RouteManager? = null

    private var mGPSLocationManager : GPSLocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        segmentListAdapter = SegmentListAdapter()
        recycleViewDirection.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleViewDirection.addItemDecoration(DividerSpaceItemDecoration(resources.getDimension(R.dimen.space_medium).toInt(), false))
        recycleViewDirection.adapter = segmentListAdapter

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mGPSLocationManager?.removeFetchingLocationUpdates()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Add marker to End Point Destination
        mMap.addMarker(MarkerOptions().position(endDestination).title(resources.getString(R.string.marker_title_nike)))

        routeManager = RouteManager(resources, mMap, this)

        mGPSLocationManager = GPSLocationManager(this, this)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mGPSLocationManager?.startFetchingLocationUpdates()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_USER_PERMISSION_ALL)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_USER_PERMISSION_ALL -> {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                    mGPSLocationManager?.startFetchingLocationUpdates()
                } else {
                    layoutLoader.visibility = View.GONE
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(MAP_ZOOM_LEVEL).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        routeManager?.routeCurrentLocation(location)
    }

    override fun onRoutingSuccess(listRoute: ArrayList<Route>) {
        layoutLoader.visibility = View.GONE
        layoutSuggestedRoute.visibility = View.VISIBLE
        txtDurationDistance.text = String.format(resources.getString(R.string.label_duration_distance), listRoute.first().durationText, listRoute.first().distanceText)
        txtPathName.text = listRoute.first().name
        segmentListAdapter?.updateList(listRoute.first().segments)
    }

    override fun onRoutingFailed(message: String) {
        layoutLoader.visibility = View.GONE
        layoutSuggestedRoute.visibility = View.GONE
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun updateSegmentAdapter(listRouteSegment: ArrayList<Segment>) {
        segmentListAdapter?.updateList(listRouteSegment)
    }
}
