package com.activityapp.burngo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val pointsOfInterest = listOf(
        PointOfInterest("KTU Miestelis", LatLng(54.904041, 23.957961)),
        PointOfInterest("Triju Mergeliu tiltas", LatLng(54.896881, 23.969057)),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Zooming out to focus on Kaunas on launch
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Add markers for each point of interest
        pointsOfInterest.forEach { poi ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
        }

        // Hardcode Kaunas coordinates
        val kaunasLatLng = LatLng(54.8985, 23.9036)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kaunasLatLng, 12f))
    }
}

data class PointOfInterest(val name: String, val latLng: LatLng)