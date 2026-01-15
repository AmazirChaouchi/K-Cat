package com.n7.api.kcat.repository

import com.n7.api.kcat.model.LitterCleanup
import com.n7.api.kcat.model.LitterMeasurement
import org.springframework.data.repository.CrudRepository

/*
Repository des donnees "LitterCleanup" faisant le lien avec la BD postgresql
 */
interface LitterCleanupRepository : CrudRepository<LitterCleanup, Int> {
    // Recuperer les donnees de nettoyage pour une litiere
    fun findByLitiereId(litiereId: String): LitterCleanup?
}