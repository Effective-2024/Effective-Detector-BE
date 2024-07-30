package com.effective.detector.hospital.api

import com.effective.detector.hospital.api.dto.HospitalResponse
import com.effective.detector.hospital.application.HospitalService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[Hospital] 병원", description = "병원 관련 기능")
@RestController
@RequestMapping("/hospitals")
class HospitalController(
    private val hospitalService: HospitalService,
) {

    @Operation(summary = "병원 검색")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun findHospitalsByKeyword(
        @RequestParam(required = false) keyword: String?,
    ): ResponseEntity<List<HospitalResponse>> {
        return ResponseEntity.ok(hospitalService.findHospitalsByKeyword(keyword))
    }
}
