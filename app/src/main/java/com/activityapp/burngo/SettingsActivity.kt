package com.activityapp.burngo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun navigateToMainActivity(view: View) {
        // Create an intent to navigate back to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish the SettingsActivity to remove it from the back stack
    }
}