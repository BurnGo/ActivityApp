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

        val AlrdHaveAccountText = findViewById<TextView>(R.id.AlrdHaveAccountText)
        AlrdHaveAccountText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val RegisterButton = findViewById<Button>(R.id.RegisterButton)
        RegisterButton.setOnClickListener {
            // Display a toast message
            Toast.makeText(this, "Sw kolega, but not implimented", Toast.LENGTH_SHORT).show()
        }
    }
}