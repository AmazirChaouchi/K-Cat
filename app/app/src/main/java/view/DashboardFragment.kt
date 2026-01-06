package view

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class DashboardFragment : Fragment() {

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

        // Graphes
        createLitterChart(view )
        createWeightChart(view)

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
            labelCount = 8   // indicatif, MPAndroidChart ajuste
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

     fun createWeightChart(view : View) {
        val chart = view.findViewById<LineChart>(R.id.weightChart)

        // Données fake : 12 semaines (~3 mois)
        val entries = mutableListOf<Entry>()
        var weight = 4.2f

        for (week in 0..11) {
            // petite variation réaliste
            weight += listOf(-0.05f, 0f, 0.03f).random()
            entries.add(Entry(week.toFloat(), weight))
        }

         val dataSet = LineDataSet(entries, "Poids du chat").apply {
             color = requireContext().getColor(android.R.color.holo_blue_dark)
             setCircleColor(requireContext().getColor(android.R.color.holo_blue_dark))
             lineWidth = 2f
             circleRadius = 4f
             setDrawCircleHole(false)

             setDrawValues(false)      // pas de valeurs sur chaque point
             mode = LineDataSet.Mode.CUBIC_BEZIER // courbe douce
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
     }

    companion object {
        private const val PREFS_NAME = "kcat_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CAT_NAME = "cat_name"
    }
}
