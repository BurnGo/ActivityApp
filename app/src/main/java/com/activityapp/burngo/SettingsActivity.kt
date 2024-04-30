package com.activityapp.burngo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        dbHelper = DBHelper(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //setContentView(R.layout.activity_settings_phone)

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

        //get the spinner from the xml.
        val dropdown = findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("Bus", "Small economic car", "Medium sized cars", "Large SUV's or minivans", "Cars with powerfull engines")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        //Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigation.selectedItemId = R.id.settings
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(Intent(this, MainActivity::class.java))
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

        val logout = findViewById<Button>(R.id.Logout)
        logout.setOnClickListener {
            dbHelper.logoutUser()
            startActivity(Intent(this, StartPageActivity::class.java))
        }

        val help = findViewById<Button>(R.id.helpButton)
        help.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

    }


//    fun navigateToStatisticActivity(view: View) {
//        // Create an intent to navigate back to the MainActivity
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish() // Optional: finish the SettingsActivity to remove it from the back stack
//    }
//    fun navigateToTreeActivity(view: View) {
//        // Create an intent to navigate back to the MainActivity
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish() // Optional: finish the SettingsActivity to remove it from the back stack
//    }
    fun navigateToMainActivity(view: View) {
        // Create an intent to navigate back to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the SettingsActivity to remove it from the back stack
    }
}