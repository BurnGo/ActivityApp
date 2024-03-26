package com.activityapp.burngo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.Calendar

class StatisticActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var caloriesTextView: TextView
    private lateinit var distanceTextView: TextView
    private val xAxisLabel = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        // Bar chart stuff
        barChart = findViewById(R.id.bar_chart)
        caloriesTextView = findViewById(R.id.CaloriesTextView)
        distanceTextView = findViewById(R.id.DistanceTextView)
        setupBarChart()
        setData()

        // Navbar stuff
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
        leftAxis.isEnabled = false

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        barChart.legend.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)
        barChart.setDrawGridBackground(false)
    }

    private fun setData() {
        val steps = floatArrayOf(1000f, 1500f, 2000f, 1800f, 2200f, 2500f, 3300f)
        val entries = ArrayList<BarEntry>()

        // Calculate the current day of the week (0: Sunday, 1: Monday, ..., 6: Saturday)
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        // Define the names of the days of the week
        val dayNames = arrayOf("P", "A", "T", "K", "Pn", "Š", "S")

        for (i in steps.indices) {
            val dayIndex = (currentDayOfWeek + i - 1) % 7 // Calculate the index of the day of the week
            entries.add(BarEntry(i.toFloat(), steps[i]))
            // Set the day name as the label for each data point on the X-axis
            xAxisLabel.add(dayNames[dayIndex])
        }


        val barDataSet = BarDataSet(entries, "Steps")
        barDataSet.color = ContextCompat.getColor(this, R.color.green)
        barDataSet.valueTextSize = 16f // Set the text size

        val data = BarData(barDataSet)
        barChart.data = data

        // Set custom labels on the X-axis
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        barChart.xAxis.textSize= 16f
        barChart.invalidate()
        barChart.setExtraOffsets(0f,0f, 0f, 15f)

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
