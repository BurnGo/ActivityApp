package com.activityapp.burngo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginButton = findViewById<TextView>(R.id.LoginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val registerButton = findViewById<Button>(R.id.RegisterButton)
        registerButton.setOnClickListener {
            // Display a toast message
            Toast.makeText(this, "Sw kolega, but not implimented", Toast.LENGTH_SHORT).show()
        }
    }
}