package api

import java.time.LocalDateTime

/*
Modele "LitterMeasurement" representant les donnees mesurees depuis les capteurs de la litiere
 */
data class LitterMeasurement(

    val id: Int,
    val litiereId: String,
    val poids: Double,
    val timestamp: String

) 