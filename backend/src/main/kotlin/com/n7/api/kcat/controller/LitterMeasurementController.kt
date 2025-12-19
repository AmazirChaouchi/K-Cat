package com.n7.api.kcat.controller

import com.n7.api.kcat.model.LitterMeasurement
import com.n7.api.kcat.repository.LitterMeasurementRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/*
Controller permettant de manipuler les donnees "LitterMeasurement"
 */
@RestController
@RequestMapping("/api/litter-measurements")
class LitterMeasurementController(private val litterMeasurementRepository: LitterMeasurementRepository) {

    /*
    Obtenir toutes les donnees "LitterMeasurements"
     */
    @GetMapping("")
    fun getAllMeasurements(): List<LitterMeasurement> =
        litterMeasurementRepository.findAll().toList()
}