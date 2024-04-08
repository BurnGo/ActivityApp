package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartPageActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        dbHelper = DBHelper(this)

        dbHelper.initialDataInsertion("Small car")
        dbHelper.initialDataInsertion("Medium car")
        dbHelper.initialDataInsertion("Large car")
        val signUpButton = findViewById<TextView>(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}