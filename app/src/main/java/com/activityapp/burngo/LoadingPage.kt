package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoadingPage : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_page)
        dbHelper = DBHelper(this)
        val activeBool = dbHelper.checkActiveUsersCount()
        //Log.d("Maldauju", activeBool.toString())
        if(activeBool){
            startActivity(Intent(this, MainActivity::class.java))
        }
        else{
            startActivity(Intent(this, StartPageActivity::class.java))
        }


    }
}