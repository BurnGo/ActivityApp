package com.activityapp.burngo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import com.activityapp.burngo.R
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {


    private lateinit var locationManager: LocationManager
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val DEFAULT_ZOOM = 15f

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private val pointsOfInterest = listOf(
        PointOfInterest("KTU Miestelis", LatLng(54.904041, 23.957961)),
        PointOfInterest("Triju Mergeliu tiltas", LatLng(54.896881, 23.969057))
        // Add more points of interest as needed
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        //Step calculations
        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Get the vector drawable resource
        val vectorDrawable = ContextCompat.getDrawable(this, R.drawable.users_location_icon)
        // Convert the vector drawable to a bitmap
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                    .position(currentLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                googleMap.addMarker(markerOptions)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                locationManager.removeUpdates(this)

                // Add markers for points of interest
                pointsOfInterest.forEach { poi ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
                    )
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reload the map
                recreate()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            val topTextProgress = findViewById<TextView>(R.id.top_text_progress)
            topTextProgress.text = ("$currentSteps")

            val progressBar = findViewById<CircularProgressBar>(R.id.circular_progress)
            progressBar.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }

        }
    }
    private fun resetSteps() {
        val topTextProgress = findViewById<TextView>(R.id.top_text_progress)
        topTextProgress.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        topTextProgress.setOnClickListener {
            previousTotalSteps = totalSteps
            topTextProgress.text = 0.toString()
            saveData()

            true
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

}

data class PointOfInterest(val name: String, val latLng: LatLng)