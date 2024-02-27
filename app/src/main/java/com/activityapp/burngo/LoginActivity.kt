package com.activityapp.burngo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpButton = findViewById<TextView>(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val loginButton = findViewById<Button>(R.id.LoginButton)
        loginButton.setOnClickListener {
            // Display a toast message
            Toast.makeText(this, "404 not found", Toast.LENGTH_SHORT).show()
        }
    }
}