package com.activityapp.burngo

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.mikephil.charting.data.BarEntry
import java.util.*

class PaidStatistics : AppCompatActivity() {

    private val co2Saved = 12
    private lateinit var barChart: AAChartView
    private lateinit var pieChart: AAChartView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paid_statistics)

        val textTrees = findViewById<TextView>(R.id.treesText)
        textTrees.text = String.format("%.1f trees", (co2Saved.toFloat()/20))

        val textDays = findViewById<TextView>(R.id.daysText)
        textDays.text = String.format("%.0f days", 20.0)

        val textDistance = findViewById<TextView>(R.id.distanceText)
        textDistance.text = String.format("%.1f km", 37.7)


        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) // Get the current month index
        val steps = arrayOf(20000f, 19330f, 17050f, 23000f, 18000f, 25500f, 29050f, 23010f, 15000f, 17504f, 10050f, 9004f)

        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val entries = mutableListOf<BarEntry>()
        val xAxisLabel = mutableListOf<String>()

        for (i in steps.indices) {
            val monthIndex = (currentMonth + i + 1) % 12 // Calculate the index of the month
            entries.add(BarEntry(i.toFloat(), steps[i]))
            xAxisLabel.add(monthNames[monthIndex])
        }

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Steps per month statistics")
            .backgroundColor("#4b2b7f00") // Set the background color to transparent
            .dataLabelsEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Steps")
                    .color("#347D37")
                    .data(entries.toTypedArray()) // Convert BarEntry list to array
            ))
            .categories(xAxisLabel.toTypedArray()) // Convert xAxisLabel list to array
            .yAxisTitle("")
            .legendEnabled(false)

        barChart = findViewById(R.id.Newchart)
        barChart.aa_drawChartWithChartModel(aaChartModel)

        //PieChart
        setupPieChart()
    }
    private fun setupPieChart() {
        val pieData: Array<Any> = arrayOf(
            arrayOf("Bus", 2.35),
            arrayOf("Small car", 1.5),
            arrayOf("SUV", 1.15)
        )

        val pieChartModel = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Types of transportation replaced")
            .backgroundColor("#4b2b7f00")
            .dataLabelsEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Time chosen")
                    .data(pieData)
            ))
            .legendEnabled(false)
            .colorsTheme(arrayOf("#347D37", "#90E28F", "#6DBE6A"))


        pieChart = findViewById(R.id.PieChart)
        pieChart.aa_drawChartWithChartModel(pieChartModel)
    }
}