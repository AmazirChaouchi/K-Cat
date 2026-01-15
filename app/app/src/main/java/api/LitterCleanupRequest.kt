package api

import java.time.LocalDateTime

data class LitterCleanupRequest(

    val litiereId: String,
    val lastCleanUpDate: String,
    val shouldBeCleanedUp: Boolean

)