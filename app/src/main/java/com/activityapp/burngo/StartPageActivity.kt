package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartPageActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        dbHelper = DBHelper(this)
        notificationHelper = NotificationHelper()


        val signUpButton = findViewById<TextView>(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

            // Example: Show a notification when SignUpButton is clicked
            val title = "Hello"
            val message = "Thank you for trying our app!"
            notificationHelper.showNotification(this, title, message)
        }

    }
}