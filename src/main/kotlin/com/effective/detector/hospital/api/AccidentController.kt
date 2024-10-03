package com.effective.detector.hospital.api

import com.effective.detector.hospital.api.dto.AccidentResponse
import com.effective.detector.hospital.application.AccidentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[Accident] 사고", description = "사고 관련 기능")
@RestController
@RequestMapping("/accidents")
class AccidentController(
    private val accidentService: AccidentService,
) {

    @Operation(summary = "전국 사고 목록 조회")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun getAll(
        @RequestParam(required = false) pageNumber: Int? = 0,
        @RequestParam(required = false) pageSize: Int? = 10,
    ): ResponseEntity<Page<AccidentResponse>> {
        return ResponseEntity.ok(accidentService.getAll(PageRequest.of(pageNumber!!, pageSize!!)))
    }
}
