package com.activityapp.burngo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var progressBar: ProgressBar
    private var stepCount = 0
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // Step Counter sensor not available on this device
            // Handle this case appropriately
        }
    }

    override fun onResume() {
        super.onResume()
        stepCount = 0
        progressBar.progress = 0
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor == stepSensor) {
                // Update step count and progress
                stepCount = it.values[0].toInt()
                updateProgress(stepCount)
            }
        }
    }

    private fun updateProgress(stepCount: Int) {
        // Update the circular progress bar based on step count
        val maxSteps = 8000 // You can adjust this value based on your preference
        val progress = ((stepCount.toFloat() / maxSteps.toFloat()) * 100).toInt()
        progressBar.progress = progress
    }
}
