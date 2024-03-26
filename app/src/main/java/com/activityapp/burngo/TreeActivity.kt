package com.activityapp.burngo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
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

    private var fertilizerCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree)
        val treeImageView = findViewById<ImageView>(R.id.treeImageView)
        var drawable = resources.getDrawable(R.drawable.xfirst0)
        var bitmap = (drawable as BitmapDrawable).bitmap
        var resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 350, true)
        var currentIterationIndex = 0

        treeImageView.setImageBitmap(resizedBitmap)


        val waterImage = findViewById<ImageView>(R.id.water_image)
        waterImage.setOnClickListener {
            Toast.makeText(this, "You watered the plant!", Toast.LENGTH_SHORT).show()
        }

        val fertilizerImage = findViewById<ImageView>(R.id.fertilizer_image)
        fertilizerImage.setOnClickListener {
            if (fertilizerCounter >= 1) {
                currentIterationIndex = (currentIterationIndex + 1)

                if (currentIterationIndex < treeIterations.size) {
                    // Change the picture of treeImageView to the next iteration
                    drawable = resources.getDrawable(treeIterations[currentIterationIndex])
                    bitmap = (drawable as BitmapDrawable).bitmap
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 350, 450, true)
                    treeImageView.setImageBitmap(resizedBitmap)

                    fertilizerCounter = 0
                } else {
                    Toast.makeText(this, "Congratulations! You've reached the final iteration.", Toast.LENGTH_SHORT).show()
                }

            } else {
                fertilizerCounter++
                Toast.makeText(this, "You fertilized the plant!", Toast.LENGTH_SHORT).show()
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
}