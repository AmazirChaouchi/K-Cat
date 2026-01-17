import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import api.LitterMeasurement
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import api.LitterCleanup;
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.emptyList

class LitterMeasurementsViewModel : ViewModel() {

    private val _data = MutableLiveData<List<LitterMeasurement>>()
    val data: LiveData<List<LitterMeasurement>> = _data

    private val _litterCleanUp = MutableLiveData<LitterCleanup>()
    val litterCleanUp: LiveData<LitterCleanup> = _litterCleanUp

    val passagesSinceCleanup = MediatorLiveData<Int>().apply {

        fun recompute() {
            val measures = data.value ?: emptyList()
            val cleanupValue = litterCleanUp.value
            value = getPassagesSinceLastCleanup(measures, cleanupValue)
        }

        addSource(data) { recompute() }
        addSource(litterCleanUp) { recompute() }
    }

    val lastCleanup = MediatorLiveData<String>().apply {

        fun recompute() {
            val cleanupValue = litterCleanUp.value
            value = getLastCleanUpFormatted(cleanupValue)
        }

        addSource(litterCleanUp) { recompute() }
    }


    val lastPoids: LiveData<Double> = data.map { list ->
        list.lastOrNull()?.poids ?: 0.0
    }

    val weightTrend = MediatorLiveData<String>().apply {
        fun recompute() {
            val measures = data.value ?: emptyList()
            value = getWeightTrend(measures)
        }

        addSource(data) { recompute() }
    }

    fun load(id: String) {
        viewModelScope.launch {
            try {
                val resultMeasurements = RetrofitInstance.api.getLitterMeasurements(id)
                _data.value = resultMeasurements

                val resultCleanup = RetrofitInstance.api.getLitterCleanup(id)
                _litterCleanUp.value = resultCleanup
            } catch (e: Exception) {
                Log.e("API", "Erreur API", e)
            }
        }
    }

    fun setCleanup(litiereId: String) {
        viewModelScope.launch {
            try {
                val body = LitterCleanup(
                    litiereId = litiereId,
                    lastCleanUpDate = LocalDateTime.now().toString(),
                    shouldBeCleanedUp = false
                )

                RetrofitInstance.api.setLitterCleanup(body)

                load(litiereId)

            } catch (e: Exception) {
                Log.e("API", "Erreur cleanup", e)
            }
        }
    }

    fun getPassagesSinceLastCleanup(
        measures: List<LitterMeasurement>,
        cleanup: LitterCleanup?
    ): Int {

        if (cleanup == null || cleanup.lastCleanUpDate.isBlank()) {
            return 0
        }

        val formatter = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.US
        )

        val cleanupDate: Date = try {
            formatter.parse(cleanup.lastCleanUpDate) ?: return 0
        } catch (e: Exception) {
            return 0
        }

        var count = 0

        for (m in measures) {
            try {
                val passageDate = formatter.parse(m.timestamp) ?: continue
                if (passageDate.after(cleanupDate)) {
                    count++
                }
            } catch (e: Exception) {
            }
        }

        return count
    }

    fun getLastCleanUpFormatted(cleanup: LitterCleanup): String {
        if (cleanup.lastCleanUpDate.isEmpty()) {
            return "--/--/---- --:--"
        }

        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.US
        )
        val outputFormat = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.FRANCE
        )

        val parse = inputFormat.parse(cleanup.lastCleanUpDate)
        return outputFormat.format(parse)
    }

    fun getWeightTrend(measures: List<LitterMeasurement>): String {
        if (measures.isEmpty()) return "Plus de données requises"

        // grouper par jour
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val measuresByDay: Map<String, List<LitterMeasurement>> = measures.groupBy {
            val date = try { sdf.parse(it.timestamp) } catch (e: Exception) { null }
            date?.let { sdf.format(it) } ?: ""
        }.filterKeys { it.isNotEmpty() }

        // 2️calcul moyenne par jour + tri chronologique
        val dailyAverages = measuresByDay
            .map { (date, dayMeasures) ->
                val avg = dayMeasures.map { it.poids }
                    .filter { !it.isNaN() }
                    .average()
                date!! to avg
            }
            .sortedBy { it.first }  //date 
            .map { it.second }      //moyenne

        if (dailyAverages.size < 30) return "Plus de données requises"

        // 3️Derniers 30 jours
        val last30 = dailyAverages.takeLast(30)

        val first15 = last30.take(15)
        val last15 = last30.takeLast(15)

        if (first15.count { !it.isNaN() } < 7 || last15.count { !it.isNaN() } < 7) {
            return "Plus de données requises"
        }

        val avgFirst = first15.filterNot { it.isNaN() }.average()
        val avgLast = last15.filterNot { it.isNaN() }.average()

        val delta = avgLast - avgFirst

        Log.d("Weight", "avgFirst = $avgFirst, avgLast = $avgLast, delta = $delta")

        // Résultat
        return when {
            delta > 0.05 -> "En hausse"
            delta < -0.05 -> "En baisse"
            else -> "Stable"
        }
    }
}
