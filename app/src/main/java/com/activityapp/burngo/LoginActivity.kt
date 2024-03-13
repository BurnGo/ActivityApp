package com.activityapp.burngo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
class LoginActivity : AppCompatActivity() {

    private lateinit var uname: EditText
    private lateinit var pword: EditText
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpButton = findViewById<TextView>(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        uname = findViewById<EditText>(R.id.LoginUsernameInput)
        pword = findViewById<EditText>(R.id.LoginPasswordInput)
        dbHelper = DBHelper(this)
        val loginButton = findViewById<Button>(R.id.LoginButton)
        loginButton.setOnClickListener {
            // Display a toast message
            val unameText = uname.text.toString()
            val pwordText = pword.text.toString()
            if(TextUtils.isEmpty(unameText) || TextUtils.isEmpty(pwordText)){
                Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show()
            }
            else{
                val checkuser = dbHelper.checkUserpass(unameText, pwordText)
                if(checkuser){
                    Toast.makeText(this, "Login was successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Wrong login info", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}