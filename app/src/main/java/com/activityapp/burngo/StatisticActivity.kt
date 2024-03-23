package com.activityapp.burngo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight



class StatisticActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var caloriesTextView: TextView
    private lateinit var distanceTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        //Bar chart stuff
        barChart = findViewById(R.id.bar_chart)
        caloriesTextView = findViewById(R.id.CaloriesTextView)
        distanceTextView = findViewById(R.id.DistanceTextView)
        setupBarChart()
        setData()

        //Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nava)
        bottomNavigation.selectedItemId = R.id.statistics
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.plant -> {
                    startActivity(Intent(this, TreeActivity::class.java))
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
    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = 0f

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        barChart.legend.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)
        barChart.setDrawGridBackground(false)
    }

    private fun setData() {
        val steps = floatArrayOf(1000f, 1500f, 2000f, 1800f, 2200f, 2500f, 3000f)
        val entries = ArrayList<BarEntry>()
        for (i in steps.indices) {
            entries.add(BarEntry(i.toFloat(), steps[i]))
        }

        val barDataSet = BarDataSet(entries, "Steps")
        barDataSet.color = Color.GREEN

        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = e?.x?.toInt() ?: return
                val selectedSteps = steps[index]
                // Update TextViews with data for the selected day
                caloriesTextView.text = "${selectedSteps * 0.05} Kcal" // Sample calculation
                distanceTextView.text = "${selectedSteps * 0.7} Km" // Sample calculation
            }

            override fun onNothingSelected() {
                // Do nothing
            }
        })
    }
}