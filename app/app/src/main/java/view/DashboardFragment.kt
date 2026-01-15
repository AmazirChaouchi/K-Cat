package view

import LitterMeasurementsViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.k_cat.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.getValue
import api.LitterMeasurement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DashboardFragment : Fragment() {

    val id = "12345";

    private val viewModel: LitterMeasurementsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dashboard_fragment, container, false)

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)

        val prefs = requireContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val userName = prefs.getString(KEY_USER_NAME, "").orEmpty()
        val catName = prefs.getString(KEY_CAT_NAME, "").orEmpty()

        tvGreeting.text = if (userName.isNotBlank()) {
            "Bonjour $userName !"
        } else {
            "Bonjour !"
        }

        tvSubtitle.text = if (catName.isNotBlank()) {
            "Voici les dernières infos sur $catName"
        } else {
            "Voici les dernières infos sur votre chat"
        }

        // Load data from API
        viewModel.loadMeasurements(id);

        // Set poids
        val tvPoids = view.findViewById<TextView>(R.id.tvWeightValue)
        viewModel.lastPoids.observe(viewLifecycleOwner) { poids ->
            tvPoids.text = "$poids kg";
        }



        // Graphes
        createLitterChart(view )

        viewModel.data.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                createWeightChart(view, list)
            }
        }

        return view
    }

    fun createLitterChart(view : View) {
        val chart = view.findViewById<BarChart>(R.id.litterChart)

        // Données fake
        val entries = mutableListOf<BarEntry>()
        for (hour in 0..23) {
            val fakePassages = (0..3).random() // à remplacer plus tard
            entries.add(BarEntry(hour.toFloat(), fakePassages.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Passages à la litière aujourd'hui")
        dataSet.color = requireContext().getColor(android.R.color.holo_blue_light)

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        chart.data = data

        chart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        chart.axisRight.isEnabled = false

        // Configuration axe X

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 3f
            setDrawGridLines(false)
            labelCount = 8
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val hour = value.toInt()
                    return if (hour % 3 == 0) "${hour}h" else ""
                }
            }
        }

        // Axe Y
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false

        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        chart.setScaleEnabled(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setPinchZoom(false)

        chart.invalidate()
    }

     /**fun createWeightChart(view : View) {
        val chart = view.findViewById<LineChart>(R.id.weightChart)

        // Données fake
        val entries = mutableListOf<Entry>()
        var weight = 4.2f

        for (week in 0..11) {
            weight += listOf(-0.05f, 0f, 0.03f).random()
            entries.add(Entry(week.toFloat(), weight))
        }

         val dataSet = LineDataSet(entries, "Poids du chat").apply {
             color = requireContext().getColor(android.R.color.holo_blue_dark)
             setCircleColor(requireContext().getColor(android.R.color.holo_blue_dark))
             lineWidth = 2f
             circleRadius = 4f
             setDrawCircleHole(false)

             setDrawValues(false)
             mode = LineDataSet.Mode.CUBIC_BEZIER
         }

         chart.data = LineData(dataSet)

         chart.axisLeft.apply {
             axisMinimum = 3.5f
             granularity = 0.1f
             valueFormatter = object : ValueFormatter() {
                 override fun getFormattedValue(value: Float): String {
                     return String.format("%.1f kg", value)
                 }
             }
         }

         chart.axisRight.isEnabled = false

         chart.xAxis.apply {
             position = XAxis.XAxisPosition.BOTTOM
             granularity = 1f
             setDrawGridLines(false)
             labelCount = 6
             valueFormatter = object : ValueFormatter() {
                 override fun getFormattedValue(value: Float): String {
                     val week = value.toInt()
                     return if (week % 2 == 0) "S$week" else ""
                 }
             }
         }

         chart.description.isEnabled = false
         chart.legend.isEnabled = false

         chart.setScaleEnabled(false)
         chart.isDoubleTapToZoomEnabled = false
         chart.setPinchZoom(false)

         chart.invalidate()
     }**/

     fun createWeightChart(view: View, measures: List<LitterMeasurement>) {
         val chart = view.findViewById<LineChart>(R.id.weightChart)
         val totalDays = 21
         val entries = mutableListOf<Entry>()

         // parse timestamps p
         val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
         val parsedMeasures = measures.mapNotNull { m ->
             try {
                 m to LocalDateTime.parse(m.timestamp, formatter)
             } catch (e: Exception) {
                 null
             }
         }

         if (parsedMeasures.isEmpty()) return

         val today = LocalDate.now()

         // organise poids par jour
         val weightsByDay = mutableMapOf<Int, Float>()
         parsedMeasures.forEach { (measure, dateTime) ->
             val date = dateTime.toLocalDate()
             val daysDiff = ChronoUnit.DAYS.between(date, today).toInt()
             if (daysDiff in 0 until totalDays) {
                 val x = totalDays - 1 - daysDiff
                 weightsByDay[x] = measure.poids.toFloat()
             }
         }

         // Crée les points pour le graphe
         for (day in 0 until totalDays) {
             val weight = weightsByDay[day] ?: Float.NaN
             entries.add(Entry(day.toFloat(), weight))
         }

         // Dataset
         val dataSet = LineDataSet(entries, "Poids du chat").apply {
             color = requireContext().getColor(android.R.color.holo_blue_dark)
             setCircleColor(requireContext().getColor(android.R.color.holo_blue_dark))
             lineWidth = 2f
             circleRadius = 4f
             setDrawCircleHole(false)
             setDrawValues(false)
             mode = LineDataSet.Mode.CUBIC_BEZIER
         }

         chart.data = LineData(dataSet)

         // Axe Y -> poids
         chart.axisLeft.apply {
             axisMinimum = 0f
             axisMaximum = (measures.maxOfOrNull { it.poids }?.toFloat()?.plus(0.5f)) ?: 5f
             granularity = 0.1f
             valueFormatter = object : ValueFormatter() {
                 override fun getFormattedValue(value: Float): String {
                     return String.format("%.1f kg", value)
                 }
             }
         }
         chart.axisRight.isEnabled = false

         // Axe X -> date
         chart.xAxis.apply {
             position = XAxis.XAxisPosition.BOTTOM
             granularity = 1f
             setDrawGridLines(false)
             labelCount = totalDays
             isGranularityEnabled = true
             setAvoidFirstLastClipping(true)
             valueFormatter = object : ValueFormatter() {
                 override fun getFormattedValue(value: Float): String {
                     val dayIndex = value.toInt()
                     val daysAgo = totalDays - 1 - dayIndex
                     val date = today.minusDays(daysAgo.toLong())
                     // Label tous les 3 jours
                     return if (daysAgo % 3 == 0 || daysAgo == 0) date.dayOfMonth.toString() else ""
                 }
             }
         }

         chart.description.isEnabled = false
         chart.legend.isEnabled = false
         chart.setScaleEnabled(false)
         chart.isDoubleTapToZoomEnabled = false
         chart.setPinchZoom(false)

         chart.invalidate()
     }


    companion object {
        private const val PREFS_NAME = "kcat_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CAT_NAME = "cat_name"
    }
}
