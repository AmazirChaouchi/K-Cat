package api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime

interface ApiService {

    @GET("litter-measurements")
    suspend fun getLitterMeasurements(
        @Query("litiereId") litiereId: String
    ): List<LitterMeasurement>

    @PUT("litter-cleanup")
    suspend fun setLitterCleanup(
        @Body body: LitterCleanupRequest
    ): Response<Unit>
}
