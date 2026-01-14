package com.n7.api.kcat.controller

import com.n7.api.kcat.model.LitterCleanup
import com.n7.api.kcat.model.LitterMeasurement
import com.n7.api.kcat.repository.LitterCleanupRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/*
Controller permettant de manipuler les donnees "LitterCleanup"
 */
@RestController
@RequestMapping("/api/litter-cleanup")
class LitterCleanupController(private val litterCleanupRepository: LitterCleanupRepository) {
    /*
        Obtenir toutes les donnees "LitterCleanup" relatives a une litiere
         */
    @GetMapping("")
    fun getCleanupInfoById(@RequestParam(name = "litiereId") litiereId: String): LitterCleanup? =
        litterCleanupRepository.findByLitiereId(litiereId)

}