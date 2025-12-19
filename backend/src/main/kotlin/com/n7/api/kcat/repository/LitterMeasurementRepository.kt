package com.n7.api.kcat.repository

import com.n7.api.kcat.model.LitterMeasurement
import org.springframework.data.repository.CrudRepository

interface LitterMeasurementRepository : CrudRepository<LitterMeasurement, Int> {
}