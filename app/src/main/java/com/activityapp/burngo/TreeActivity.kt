package com.activityapp.burngo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class TreeActivity : AppCompatActivity() {

    private val treeIterations = listOf(
        R.drawable.xfirst0,
        R.drawable.xfirst1,
        R.drawable.xfirst2,
        R.drawable.xfirst3,
        R.drawable.xfirst4,
        R.drawable.xsecond1,
        R.drawable.xsecond2,
        R.drawable.xsecond3,
        R.drawable.xsecond4,
        R.drawable.xthird1,
        R.drawable.xthird2,
        R.drawable.xthird3,
        R.drawable.xthird4
    )

    private var balance = 800
    private lateinit var waterProgressBar: com.mackhartley.roundedprogressbar.RoundedProgressBar
    private var waterCounter = 0
    private lateinit var fertilizerProgressBar: com.mackhartley.roundedprogressbar.RoundedProgressBar
    private var fertilizerCounter = 0
    private var currentIterationIndex = 0

    private lateinit var drawable: Drawable
    private lateinit var bitmap: Bitmap
    private lateinit var resizedBitmap: Bitmap
    private lateinit var treeImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree)

        treeImageView = findViewById(R.id.treeImageView)
        drawable = resources.getDrawable(R.drawable.xfirst0)
        bitmap = (drawable as BitmapDrawable).bitmap
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, 250, 300, true)

        treeImageView.setImageBitmap(resizedBitmap)

        val balanceTextView = findViewById<TextView>(R.id.balanceTextView)
        updateBalanceTextView(balanceTextView)
        currentIterationIndex=0
        waterProgressBar = findViewById(R.id.progress_bar_water)
        fertilizerProgressBar = findViewById(R.id.progress_bar_fertilizer)

        val waterImage = findViewById<ImageView>(R.id.water_image)
        val fertilizerImage = findViewById<ImageView>(R.id.fertilizer_image)

        waterImage.setOnClickListener {
            if (canPurchase()) {
                balance -= 10
                updateBalanceTextView(balanceTextView)
                waterCounter++
                val waterProgressPercentage = (waterCounter.toFloat() / 4) * 100
                waterProgressBar.setProgressPercentage(waterProgressPercentage.toDouble())

                if (waterCounter == 4 && fertilizerCounter == 4) {
                    progressPlant()
                }
                Toast.makeText(this, "You watered the plant!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Not enough coins to purchase!", Toast.LENGTH_SHORT).show()
            }
        }

        fertilizerImage.setOnClickListener {
            if (canPurchase()) {
                balance -= 10
                updateBalanceTextView(balanceTextView)
                fertilizerCounter++
                val fertilizerProgressPercentage = (fertilizerCounter.toFloat() / 4) * 100
                fertilizerProgressBar.setProgressPercentage(fertilizerProgressPercentage.toDouble())

                if (waterCounter == 4 && fertilizerCounter == 4) {
                    progressPlant()
                }

                Toast.makeText(this, "You fertilized the plant!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Not enough coins to purchase!", Toast.LENGTH_SHORT).show()
            }
        }

        //Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nava)
        bottomNavigation.selectedItemId = R.id.plant
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.statistics -> {
                    startActivity(Intent(this, StatisticActivity::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }
    private fun progressPlant() {

        if (currentIterationIndex < treeIterations.size - 1) { // Check if it's not the last iteration
            if (currentIterationIndex <= 3) {
                currentIterationIndex++
                drawable = resources.getDrawable(treeIterations[currentIterationIndex])
                bitmap = (drawable as BitmapDrawable).bitmap
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 250, 300, true)
                Toast.makeText(this, "Plant has progressed!", Toast.LENGTH_SHORT).show()

                waterCounter = 0
                fertilizerCounter = 0
                waterProgressBar.setProgressPercentage(0.0)
                fertilizerProgressBar.setProgressPercentage(0.0)
            } else {
                currentIterationIndex++
                drawable = resources.getDrawable(treeIterations[currentIterationIndex])
                bitmap = (drawable as BitmapDrawable).bitmap
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, 375, 480, true)
                Toast.makeText(this, "Plant has progressed!", Toast.LENGTH_SHORT).show()

                waterCounter = 0
                fertilizerCounter = 0
                waterProgressBar.setProgressPercentage(0.0)
                fertilizerProgressBar.setProgressPercentage(0.0)
            }
        } else {
            Toast.makeText(this, "Congratulations! You've reached the final iteration.", Toast.LENGTH_SHORT).show()
        }
        treeImageView.setImageBitmap(resizedBitmap)
    }
    private fun updateBalanceTextView(textView: TextView) {
        textView.text = "$balance"
    }

    private fun canPurchase(): Boolean {
        return balance >= 10
    }
}
