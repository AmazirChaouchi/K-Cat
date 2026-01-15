package com.n7.api.kcat.controller

import com.n7.api.kcat.model.LitterCleanup
import com.n7.api.kcat.model.LitterMeasurement
import com.n7.api.kcat.repository.LitterCleanupRepository
import com.n7.api.kcat.repository.LitterMeasurementRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/*
Controller permettant de manipuler les donnees "LitterMeasurement"
 */
@RestController
@RequestMapping("/api/litter-measurements")
class LitterMeasurementController(private val litterMeasurementRepository: LitterMeasurementRepository,
                                        private val litterCleanupRepository: LitterCleanupRepository) {

    /*
    Obtenir toutes les donnees "LitterMeasurements" relatives a une litiere
     */
    @GetMapping("")
    fun getMeasurementsById(@RequestParam(name = "litiereId") litiereId: String): List<LitterMeasurement> =
            litterMeasurementRepository.findByLitiereId(litiereId)

    /*
    Créer une nouvelle donnee "LitterMeasurements"
     */
    @PostMapping("")
    fun newMeasurement(@RequestBody litterMeasurement: LitterMeasurement) : ResponseEntity<Boolean> {
        if(litterMeasurement.litiereId == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val createdMeasurement = litterMeasurementRepository.save(litterMeasurement)

        //verification du nettoyage
        var cleanUpData = litterCleanupRepository.findByLitiereId(litterMeasurement.litiereId!!)
        var mustBeCleaned = false

        // si la donnee n'existe pas on la cree
        if (cleanUpData == null) {
            cleanUpData = LitterCleanup(
                litiereId = litterMeasurement.litiereId!!,
                lastCleanUpDate = LocalDateTime.now().minusMinutes(1),  //on indique arbitrairement un premier nettoyage une minute avant le premier passage
                shouldBeCleanedUp = false
            )
            cleanUpData = litterCleanupRepository.save(cleanUpData)
        } else {
            // sinon on regarde s'il faut nettoyer
            val lastCleanUp = cleanUpData.lastCleanUpDate
            val nbPassages = litterMeasurementRepository.findByLitiereId(litterMeasurement.litiereId!!).count { mesure ->
                mesure.timestamp?.isAfter(lastCleanUp) == true
            }

            mustBeCleaned = nbPassages >= 3;
            if(mustBeCleaned) {
                cleanUpData.shouldBeCleanedUp = true
                litterCleanupRepository.save(cleanUpData)
            }
        }

        return ResponseEntity(mustBeCleaned, HttpStatus.CREATED)
    }
}