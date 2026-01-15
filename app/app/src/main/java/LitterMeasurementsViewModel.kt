import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import api.LitterMeasurement
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import api.LitterCleanupRequest;

class LitterMeasurementsViewModel : ViewModel() {

    private val _data = MutableLiveData<List<LitterMeasurement>>()
    val data: LiveData<List<LitterMeasurement>> = _data

    val lastPoids: LiveData<Double> = data.map { list ->
        list.lastOrNull()?.poids ?: 0.0
    }

    fun loadMeasurements(id: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getLitterMeasurements(id)
                _data.value = result
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
                val body = LitterCleanupRequest(
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

}
