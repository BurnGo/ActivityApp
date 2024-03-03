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
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.Marker
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.*
import kotlin.random.Random


class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener, LocationListener  {


    private lateinit var locationManager: LocationManager
    private lateinit var googleMap: GoogleMap
    private lateinit var progressBar: CircularProgressBar

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val DEFAULT_ZOOM = 15f
    private var userMarker: Marker? = null

    private val rewards = mutableListOf<Reward>()
    private val MAX_DAILY_REWARDS = 5
    private val REWARD_RADIUS_METERS = 2000
    private val REWARD_PICKUP_DISTANCE = 20 // in meters

    private var sensorManager: SensorManager? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private val STEP_THRESHOLD = 5f // Adjust as needed
    private var lastAcceleration = 0f

    val usersLocationIcon = R.drawable.users_location_icon

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

        //Progress bar settings
        progressBar = findViewById(R.id.circular_progress)
        progressBar.apply { progressMax=8000f }

        //Step calculations
        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //this is temporary to test progress bar
        val simulateButton = findViewById<Button>(R.id.simulate_button)
        simulateButton.setOnClickListener {
            // Increment the totalSteps variable to simulate steps taken
            totalSteps += 10f // Increment by 10 steps (adjust as needed)

            // Update UI with the new step count
            updateStepCountUI(totalSteps.toInt())
        }

        val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No accelerometer sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        startLocationUpdates()


        // Add markers for points of interest
        addMarkersForPointsOfInterest()
    }

    private fun addMarkersForPointsOfInterest() {
        pointsOfInterest.forEach { poi ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
        }
    }

    private fun addMarkerWithCustomIcon(location: Location, iconResourceId: Int, width: Int, height: Int) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        // Get the vector drawable resource and convert it to a bitmap
        val vectorDrawable = ContextCompat.getDrawable(this, iconResourceId)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)

        // Create marker options with the custom icon
        val markerOptions = MarkerOptions()
            .position(currentLatLng)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

        // Add the marker to the map
        googleMap.addMarker(markerOptions)
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
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val acceleration = calculateMagnitude(event.values)
            val delta = acceleration - lastAcceleration
            lastAcceleration = acceleration

            if (delta > STEP_THRESHOLD) {
                // A step is detected
                totalSteps++
                Log.d("Steps", "Steps added")
                updateStepCountUI(totalSteps.toInt())
            }
        }
    }
    //helper function for step counting
    private fun calculateMagnitude(values: FloatArray): Float {
        // Calculate the magnitude of acceleration
        return sqrt((values[0] * values[0] + values[1] * values[1] + values[2] * values[2]).toDouble()).toFloat()
    }

    private fun updateStepCountUI(stepCount: Int) {
        // Update the UI with the current step count
        val topTextProgress = findViewById<TextView>(R.id.top_text_progress)
        topTextProgress.text = stepCount.toString()

        progressBar.setProgressWithAnimation(stepCount.toFloat())
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
        Log.d("SensorAccuracy", "Sensor accuracy changed: $accuracy")
    }

    override fun onLocationChanged(location: Location) {
        userMarker?.remove()
        val currentLatLng = LatLng(location.latitude, location.longitude)
        addMarkerWithCustomIcon(location, usersLocationIcon, 100, 100)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))

        // Generate daily rewards if not already generated
        if (rewards.isEmpty()) {
            generateDailyRewards(location)
        }

        // Check proximity to rewards
        val iterator = rewards.iterator()
        while (iterator.hasNext()) {
            val reward = iterator.next()
            val distance = calculateDistance(location.latitude, location.longitude, reward.location.latitude, reward.location.longitude)
            if (distance <= REWARD_PICKUP_DISTANCE && !reward.pickedUp) {
                // Reward is within pickup distance and not picked up yet
                reward.pickedUp = true
                iterator.remove()
                updateMap()
                showRewardPickedUpMessage()
            }
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, this)
        }
    }
    private fun generateDailyRewards(userLocation: Location) {
        for (i in 1..MAX_DAILY_REWARDS) {
            val randomLatLng = generateRandomLatLng(userLocation, REWARD_RADIUS_METERS)
            rewards.add(Reward(randomLatLng))
        }
        updateMap()
    }
    private fun generateRandomLatLng(center: Location, radius: Int): LatLng {
        val randomDistance = Random.nextDouble(0.0, radius.toDouble())
        val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
        val randomLatitude = center.latitude + (randomDistance * cos(randomAngle)) / 111111
        val randomLongitude = center.longitude + (randomDistance * sin(randomAngle)) / (111111 * cos(center.latitude * Math.PI / 180))
        return LatLng(randomLatitude, randomLongitude)
    }
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val R = 6371000 // Radius of the Earth in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (R * c).toFloat()
    }
    private fun updateMap() {
        googleMap.clear()
        rewards.forEach { reward ->
            googleMap.addMarker(MarkerOptions().position(reward.location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        }
    }
    private fun showRewardPickedUpMessage() {
        Toast.makeText(this, "Reward picked up!", Toast.LENGTH_SHORT).show()
    }

}

data class PointOfInterest(val name: String, val latLng: LatLng)
data class Reward(val location: LatLng, var pickedUp: Boolean = false)