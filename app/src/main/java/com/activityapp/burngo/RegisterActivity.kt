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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


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

        val btn: TextView = findViewById(R.id.tempGoToMain)
        btn.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, StartPageActivity::class.java))
        }

        //Temporary button to go from register to maps window
        val btnMaps: TextView = findViewById(R.id.GoToMapsTemp)
        btnMaps.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))//-----
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






                if (TextUtils.isEmpty(unameText) || TextUtils.isEmpty(emailText) || TextUtils.isEmpty(
                        pwordText
                    ) || TextUtils.isEmpty(confirmpwordText)
                ) {
                    Toast.makeText(this, "Missing info", Toast.LENGTH_SHORT).show()
                } else {
                    if (pwordText.equals(confirmpwordText)) {
                        dbHelper.insertData(unameText, emailText, pwordText, this)
/*
                        //serverRegister(unameText, emailText, pwordText)
                        if (saveData) {
                            Toast.makeText(this, "Register was successful", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else {

                            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                        }*/
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception){
                Log.d("Register was unsuccessful", e.message.toString())
            }
        }
    }

    fun serverRegister(name: String, email: String, pass: String){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2/register.php"

        val params = HashMap<String, String>()
        params["name"] = name
        params["pass"] = pass
        params["email"] = email

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "$response", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        queue.add(stringRequest)
    }
}

