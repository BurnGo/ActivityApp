package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RegisterActivity : AppCompatActivity() {

    private lateinit var uname: EditText;
    private lateinit var email: EditText;
    private lateinit var pword: EditText;
    private lateinit var confirmpword: EditText;
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        uname = findViewById(R.id.LoginUsernameInput)
        email = findViewById(R.id.LoginEmailInput)
        pword = findViewById(R.id.PasswordInput)
        confirmpword = findViewById(R.id.ConfirmPasswordInput)
        dbHelper = DBHelper(this)


        //Temporary button to go from register to maps window
        val btnMaps: TextView = findViewById(R.id.GoToMapTemp)
        btnMaps.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, StatisticActivity::class.java))//-----
        }

        val btnHelp: TextView = findViewById(R.id.GoToHelpTemp)
        btnHelp.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, HelpActivity::class.java))//-----
        }

        val loginButton = findViewById<TextView>(R.id.LoginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val registerButton = findViewById<Button>(R.id.RegisterButton)
        registerButton.setOnClickListener {
            // Display a toast message
            try {
                val unameText = uname.text.toString()
                val emailText = email.text.toString()
                val pwordText = pword.text.toString()
                val confirmpwordText = confirmpword.text.toString()
                val saveData = dbHelper.insertData(unameText, emailText, pwordText)




                if (TextUtils.isEmpty(unameText) || TextUtils.isEmpty(emailText) || TextUtils.isEmpty(
                        pwordText
                    ) || TextUtils.isEmpty(confirmpwordText)
                ) {
                    Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show()
                } else {
                    if (pwordText.equals(confirmpwordText)) {

                        if (saveData) {
                            Toast.makeText(this, "Register was successful", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else {

                            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception){
                Log.d("KURWA", e.message.toString())
            }
        }
    }
}

