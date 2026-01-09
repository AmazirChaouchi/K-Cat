package com.n7.api.kcat.repository

import com.n7.api.kcat.model.LitterMeasurement
import org.springframework.data.repository.CrudRepository

/*
Repository des donnees "LitterMeasurement" faisant le lien avec la BD postgresql
 */
interface LitterMeasurementRepository : CrudRepository<LitterMeasurement, Int> {
    // Recuperer toutes les donnees pour une litiere
    fun findByLitiereId(litiereId: String): List<LitterMeasurement>
}