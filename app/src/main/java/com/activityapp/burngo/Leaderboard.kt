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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlin.math.log
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager

class Leaderboard : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var yourPlaceView: RecyclerView
    private lateinit var yourPlaceAdapter: LeaderboardUserAdapter
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.recycler_view_leaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        yourPlaceView = findViewById(R.id.your_place)
        yourPlaceView.layoutManager = LinearLayoutManager(this)



        session = Session(this)
        val userId = session.getId()

        dbHelper = DBHelper(this)
        dbHelper.getAllUsersWithSteps(this) { users ->
            Log.d("Leaderboard", "Users received: ${users.size}")
            val leaderboardUsers = processUsers(users)
            adapter = LeaderboardAdapter(leaderboardUsers.take(5)) // Display only top 5 users

            recyclerView.adapter = adapter

            yourPlaceAdapter = LeaderboardUserAdapter(leaderboardUsers.find { it.id == userId })
            yourPlaceView.adapter = yourPlaceAdapter
        }


//        dbHelper = DBHelper(this)
//        dbHelper.getAllUsersWithSteps(this)
//            { users ->
//                try {
//
//                    //Čia gali būt problemytė
//                    //Šita dalis kodo yra įvykdoma pirmiau, negu grąžina rezultatus iš DB
//                    //Kiek supratau vienintelis įmanomas išsigelbėjimas yra viską,
//                    //kas priklauso nuo duomenų vykdyti funkcijoje
//                    //Priešingu atvejų mūsų leaderboardui RIP
//                    //Tas pats galiotu ir žingsniams su statistikom
//
//                    //P.S. User ir Steps klasės yra DBHelperyje
//                    processUsers(users)
//                }
//                catch (e: Exception){
//                    Log.d("Names", e.message.toString())
//                }
//            }

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
    private fun processUsers(users: List<User>): List<LeaderboardUser> {
        // Sort users by steps in descending order
        val sortedUsers = users.sortedByDescending { it.steps }

        // Convert to LeaderboardUser and assign the place
        val leaderboardUsers = sortedUsers.mapIndexed { index, user ->
            LeaderboardUser(user.id, user.name, user.steps, index + 1)
        }

        // Log the leaderboard users
        leaderboardUsers.forEach { leaderboardUser ->
            Log.d("processUsers", "User: ${leaderboardUser.name} with Max Steps: ${leaderboardUser.steps}, Place: ${leaderboardUser.place}")
        }

        // Now, leaderboardUsers contains the sorted users with their place assigned
        return leaderboardUsers
    }
}
data class LeaderboardUser(val id: Int, val name: String, val steps: Int, val place: Int)


class LeaderboardAdapter(private val leaderboardUsers: List<LeaderboardUser>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_user, parent, false)
        return LeaderboardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val user = leaderboardUsers[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = leaderboardUsers.size

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageUserView: ImageView = itemView.findViewById(R.id.image_user)
        private val placeTextView: TextView = itemView.findViewById(R.id.text_place)
        private val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        private val stepsTextView: TextView = itemView.findViewById(R.id.text_steps)
        fun bind(user: LeaderboardUser) {
            // Set place image based on user's place
            val placeDrawableId = when (user.place) {
                1 -> R.drawable.first_place
                2 -> R.drawable.second_place
                3 -> R.drawable.third_place
                else -> R.drawable.default_place // Default placeholder image
            }
            imageUserView.setImageResource(placeDrawableId)

            placeTextView.text = user.place.toString()
            nameTextView.text = user.name
            stepsTextView.text = user.steps.toString()
        }

    }
}

class LeaderboardUserAdapter(private val user: LeaderboardUser?) :
    RecyclerView.Adapter<LeaderboardUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        user?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (user != null) 1 else 0

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageUserView: ImageView = itemView.findViewById(R.id.image_user)
        private val placeTextView: TextView = itemView.findViewById(R.id.text_place)
        private val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        private val stepsTextView: TextView = itemView.findViewById(R.id.text_steps)

        fun bind(user: LeaderboardUser) {
            // Set place image based on user's place
            val placeDrawableId = when (user.place) {
                1 -> R.drawable.first_place
                2 -> R.drawable.second_place
                3 -> R.drawable.third_place
                else -> R.drawable.default_place // Default placeholder image
            }
            imageUserView.setImageResource(placeDrawableId)

            placeTextView.text = user.place.toString()
            nameTextView.text = user.name
            stepsTextView.text = user.steps.toString()
        }
    }
}
