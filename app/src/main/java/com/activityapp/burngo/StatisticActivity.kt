package com.activityapp.burngo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
    private lateinit var stepsTextView: TextView
    private lateinit var session: Session
    private lateinit var CoTextView: TextView

    private val xAxisLabel = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        //setContentView(R.layout.activity_statistic_phone)

        session = Session(this)


        // Bar chart stuff
        barChart = findViewById(R.id.bar_chart)
        caloriesTextView = findViewById(R.id.CaloriesTextView)
        distanceTextView = findViewById(R.id.DistanceTextView)
        stepsTextView = findViewById(R.id.StepTextView)
        CoTextView = findViewById(R.id.coTextView)

        setupBarChart()
        setData()

        //TEMPORARY
        val paidStatisticsButton = findViewById<Button>(R.id.PaidButton)
        paidStatisticsButton.setOnClickListener {
            startActivity(Intent(this, PaidStatistics::class.java))
        }


        // Navbar stuff
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nava)
        bottomNavigation.selectedItemId = R.id.statistics
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.leaderboard -> {
                    startActivity(Intent(this, Leaderboard::class.java))
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
        val steps = floatArrayOf(1800f, 1930f, 2000f, 1800f, 2200f, 2500f, 3300f)
        val entries = ArrayList<BarEntry>()

        // Calculate the current day of the week (0: Sunday, 1: Monday, ..., 6: Saturday)
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        // Define the names of the days of the week
        val dayNames = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

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
        barChart.setExtraOffsets(0f,0f, 0f, 5f)

        //this can be changed to adapt the logic of database
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val userHeight = sharedPreferences.getInt("height", 180)
        val userWeight = sharedPreferences.getInt("weight", 80)


        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = e?.x?.toInt() ?: return
                val selectedSteps = steps[index]
                val distance = ((userHeight * 0.85)/1000 * selectedSteps * 5)/1000
                // Update TextViews with data for the selected day
                caloriesTextView.text = String.format("%.1f Kcal", ((1.25*userWeight)*distance)) // Sample calculation
                distanceTextView.text = String.format("%.1f Km", distance)
                stepsTextView.text = String.format("%.0f", selectedSteps)
                CoTextView.text = String.format("%.1f kg CO2", (distance*0.28785))
            }

            override fun onNothingSelected() {
                // Do nothing
            }
        })
    }
}
