package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoadingPage : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_page)
        dbHelper = DBHelper(this)
        session = Session(this)

        //Log.d("Maldauju", activeBool.toString())
        if(session.isLoggedIn()){
            startActivity(Intent(this, StatisticActivity::class.java))
        }
        else{
            startActivity(Intent(this, StartPageActivity::class.java))
        }


    }
}