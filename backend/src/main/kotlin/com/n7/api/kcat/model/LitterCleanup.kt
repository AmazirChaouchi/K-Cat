package com.n7.api.kcat.model

import jakarta.persistence.*
import java.time.LocalDateTime

/*
Modele "LitterCleanup" representant les données concernant le nettoyage d'une litière
 */
@Entity
@Table
data class LitterCleanup(

    @Id
    val litiereId: String? = null,
    var lastCleanUpDate: LocalDateTime? = null,
    var shouldBeCleanedUp: Boolean? = null

)