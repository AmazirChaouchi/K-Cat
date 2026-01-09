package com.n7.api.kcat.model

import jakarta.persistence.*
import java.time.LocalDateTime

/*
Modele "LitterMeasurement" representant les donnees mesurees depuis les capteurs de la litiere
 */
@Entity
@Table
data class LitterMeasurement(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    val litiereId: String? = null,
    val poids: Double? = null,
    val timestamp: LocalDateTime? = null

)