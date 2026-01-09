package com.n7.api.kcat.controller

import com.n7.api.kcat.model.LitterMeasurement
import com.n7.api.kcat.repository.LitterMeasurementRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    fun getMeasurementsById(@RequestParam(name = "litiereId") litiereId: String): List<LitterMeasurement> =
            litterMeasurementRepository.findByLitiereId(litiereId)

    @PostMapping("")
    fun newMeasurement(@RequestBody litterMeasurement: LitterMeasurement) : ResponseEntity<LitterMeasurement> {
        val createdMeasurement = litterMeasurementRepository.save(litterMeasurement)
        return ResponseEntity(createdMeasurement, HttpStatus.CREATED)
    }
}