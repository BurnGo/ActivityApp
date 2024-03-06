package com.activityapp.burngo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        // Set up SharedPreferences
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get the EditText for step goal input
        val inputStepGoal = findViewById<EditText>(R.id.inputStepGoal)

        // Load the stored step goal value
        val stepGoal = sharedPreferences.getInt("stepGoal", 8000)
        inputStepGoal.setText(stepGoal.toString())

        // Listen for changes to the step goal input
        inputStepGoal.addTextChangedListener {
            val newStepGoal = it.toString().toIntOrNull() ?: 8000 // Default to 8000 if parsing fails
            // Save the new step goal value in SharedPreferences
            editor.putInt("stepGoal", newStepGoal)
            editor.apply()
        }
    }

    fun navigateToMainActivity(view: View) {
        // Create an intent to navigate back to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the SettingsActivity to remove it from the back stack
    }
}