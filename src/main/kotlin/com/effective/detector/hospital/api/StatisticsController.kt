package com.effective.detector.hospital.api

import com.effective.detector.hospital.api.dto.AccidentMonthlyResponse
import com.effective.detector.hospital.api.dto.AccidentYearlyResponse
import com.effective.detector.hospital.application.StatisticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[Hospital Statistics] 병원 통계", description = "병원 통계 관련 기능")
@RestController
@RequestMapping("/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService,
) {

    @Operation(summary = "전국사고 건 수 통계 조회(월별)")
    @PreAuthorize("permitAll()")
    @GetMapping("/month")
    fun getStatisticsByMonth(
        @RequestParam(required = false) year: Int,
    ): ResponseEntity<List<AccidentMonthlyResponse>> {
        return ResponseEntity.ok(statisticsService.getStatisticsByMonth(year))
    }

    @Operation(summary = "전국사고 건 수 통계 조회(연도별)")
    @PreAuthorize("permitAll()")
    @GetMapping("/year")
    fun getStatisticsByYear(): ResponseEntity<List<AccidentYearlyResponse>> {
        return ResponseEntity.ok(statisticsService.getStatisticsByYear())
    }
}
