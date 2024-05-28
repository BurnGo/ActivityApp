package com.activityapp.burngo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay

class Leaderboard : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)

        dbHelper = DBHelper(this)
        dbHelper.getAllUsersWithSteps(this)
            { users ->
                try {


                    //Čia gali būt problemytė
                    //Šita dalis kodo yra įvykdoma pirmiau, negu grąžina rezultatus iš DB
                    //Kiek supratau vienintelis įmanomas išsigelbėjimas yra viską,
                    //kas priklauso nuo duomenų vykdyti funkcijoje
                    //Priešingu atvejų mūsų leaderboardui RIP
                    //Tas pats galiotu ir žingsniams su statistikom

                    //P.S. User ir Steps klasės yra DBHelperyje
                    processUsers(users)
                }
                catch (e: Exception){
                    Log.d("Names", e.message.toString())
                }
            }


        //Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nava)
        bottomNavigation.selectedItemId = R.id.leaderboard
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.statistics -> {
                    startActivity(Intent(this, StatisticActivity::class.java))
                    true
                }
                R.id.map -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.plant -> {
                    startActivity(Intent(this, TreeActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    private fun processUsers(users: List<User>) {
        // Process the list of users
        users.forEach { user ->
            Log.d("Names", "User: ${user.name}")
        }
    }
}

