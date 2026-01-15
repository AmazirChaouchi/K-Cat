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


    val lastPoids: LiveData<Double> = data.map { list ->
        list.lastOrNull()?.poids ?: 0.0
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

    fun getPoids(): Double {
        val last = _data.value?.lastOrNull()
        var poids = 0.0

        if (last != null) {
           poids = last.poids
        }
        return poids
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

}
