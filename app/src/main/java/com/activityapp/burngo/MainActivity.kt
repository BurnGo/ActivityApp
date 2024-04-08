package com.activityapp.burngo

import android.Manifest
import android.content.Context
import android.content.Intent
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
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.*
import kotlin.random.Random


class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener, LocationListener  {


    private lateinit var locationManager: LocationManager
    private lateinit var googleMap: GoogleMap
    private lateinit var progressBar: CircularProgressBar

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val DEFAULT_ZOOM = 17f
    private var userMarker: Marker? = null
    private lateinit var bitmap: Bitmap
    private val markerList = mutableListOf<Marker>()
    private val rewards = mutableListOf<Reward>()

    private val NUMBER_OF_POI = 2
    private val MAX_DAILY_REWARDS = 3
    private val REWARD_PROBABILITY_THRESHOLD = 0.5
    private var coinBalance = 0
    private val REWARD_MAXIMUM_RADIUS_METERS = 2000
    private val REWARD_MINIMUM_RADIUS_METERS = 300
    private val REWARD_PICKUP_DISTANCE = 30 // in meters

    private var sensorManager: SensorManager? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private val STEP_THRESHOLD = 5f
    private var lastAcceleration = 0f

    private val pointsOfInterest = listOf(
        PointOfInterest("KTU Miestelis", LatLng(54.904041, 23.957961)),
        PointOfInterest("Triju Mergeliu tiltas", LatLng(54.896881, 23.969057)),
        PointOfInterest("Azuolynas", LatLng( 54.901206, 23.949372)),
        PointOfInterest("Dainu slenis", LatLng(54.894734, 23.943053)),
        PointOfInterest("Vienybes aikste", LatLng(54.899160, 23.912995))
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
        // Load user input from SharedPreferences
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val stepGoal = sharedPreferences.getInt("stepGoal", 8000)

        // Update the progressMax of the CircularProgressBar
        progressBar.progressMax = stepGoal.toFloat()

        // Update the bottom_text_progress TextView
        val bottomTextProgress = findViewById<TextView>(R.id.bottom_text_progress)
        bottomTextProgress.text = "/$stepGoal"

        //Step calculations
        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No accelerometer sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
        }

        //Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nava)
        bottomNavigation.selectedItemId = R.id.map
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.statistics -> {
                    startActivity(Intent(this, StatisticActivity::class.java))
                    true
                }
                R.id.plant -> {
                    startActivity(Intent(this, TreeActivity::class.java))
                    true
                }
                else -> false
            }
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

        // Get the vector drawable resource
        val vectorDrawable = ContextCompat.getDrawable(this, R.drawable.users_location_icon)
        // Convert the vector drawable to a bitmap
        bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

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
        //update the users location
        val currentLatLng = LatLng(location.latitude, location.longitude)
        userMarker?.remove()
        userMarker = googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Your Location").icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))

        // Generate daily rewards if not already generated
        if (rewards.isEmpty()) {
            generatePoints(location)
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

                // Remove the corresponding reward marker from the map
                for (marker in markerList) {
                    if (marker.position == reward.location) {
                        val circle = marker.tag as Circle
                        marker.remove() // Remove the marker from the map
                        circle.remove() //Remove markers circle of pickup
                        markerList.remove(marker) // Remove the marker from the list
                        break // Exit the loop after removing the marker
                    }
                }

                showRewardPickedUpMessage()

                if (Random.nextDouble() <= REWARD_PROBABILITY_THRESHOLD) {
                    // Award the player a coin
                    awardCoin()
                }
            }
        }

        // Check if all rewards are picked up
        if (rewards.isEmpty()) {
            showAllRewardsPickedUpMessage()
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, this)
        }
    }
    private fun generatePoints(userLocation: Location) {
        for (i in 1..MAX_DAILY_REWARDS) {
            val randomLatLng = generateRandomLatLng(userLocation, REWARD_MAXIMUM_RADIUS_METERS)
            rewards.add(Reward("Randomly selected", randomLatLng))
        }

        val shuffledPointsOfInterest = pointsOfInterest.shuffled()
        // Add markers for the specified number of points in the shuffled list
        for (i in 0 until min(NUMBER_OF_POI, shuffledPointsOfInterest.size)) {
            val poi = shuffledPointsOfInterest[i]
            rewards.add(Reward(poi.name, poi.latLng))
        }

        updateMap()
    }
    private fun generateRandomLatLng(center: Location, radius: Int): LatLng {
        val randomDistance = Random.nextDouble(REWARD_MINIMUM_RADIUS_METERS.toDouble(), radius.toDouble())
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
    private fun awardCoin() {
        val coinBalanceTextView = findViewById<TextView>(R.id.coin_balance)
        coinBalance++
        coinBalanceTextView.text = "Coin Balance: $coinBalance"
    }
    private fun updateMap() {
        // Get the vector drawable resource and convert it to a bitmap
        val vectorDrawable = ContextCompat.getDrawable(this, R.drawable.reward_icon)
        val bitmap = Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)


        rewards.forEach { reward ->
            val marker = googleMap.addMarker(MarkerOptions().position(reward.location).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(reward.name))
            if (marker != null) {
                markerList.add(marker)
            }

            // Add circle overlay for reward pickup distance
            val circleOptions = CircleOptions()
                .center(reward.location)
                .radius(REWARD_PICKUP_DISTANCE.toDouble())
                .strokeColor(Color.argb(100, 0, 0, 255)) //outer ring
                .fillColor(Color.argb(70, 0, 0, 255)) //inner ring
            val circle = googleMap.addCircle(circleOptions)

            marker?.tag = circle
        }
    }

    private fun showRewardPickedUpMessage() {
        Toast.makeText(this, "Reward picked up!", Toast.LENGTH_SHORT).show()
    }
    private fun showAllRewardsPickedUpMessage() {
        Toast.makeText(this, "Congratulations, you picked up all the daily rewards!", Toast.LENGTH_SHORT).show()
    }
    fun navigateToSettingsActivity(view: View) {
        // Create an intent to navigate back to the MainActivity
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the SettingsActivity to remove it from the back stack
    }

}

data class PointOfInterest(val name: String, val latLng: LatLng)
data class Reward(val name: String, val location: LatLng, var pickedUp: Boolean = false)