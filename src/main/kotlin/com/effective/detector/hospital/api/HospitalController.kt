package com.effective.detector.hospital.api

import com.effective.detector.common.annotation.LoginMember
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.hospital.api.dto.AccidentMonthlyResponse
import com.effective.detector.hospital.api.dto.AccidentYearlyResponse
import com.effective.detector.hospital.api.dto.HospitalResponse
import com.effective.detector.hospital.application.HospitalService
import com.effective.detector.hospital.application.ValidateService
import com.effective.detector.member.domain.Member
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[Hospital] 병원", description = "병원 관련 기능")
@RestController
@RequestMapping("/hospitals")
class HospitalController(
    private val hospitalService: HospitalService,
    private val validateService: ValidateService,
) {

    @Operation(summary = "병원 검색")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun findHospitalsByKeyword(
        @RequestParam(required = false) keyword: String?,
    ): ResponseEntity<List<HospitalResponse>> {
        return ResponseEntity.ok(hospitalService.findHospitalsByKeyword(keyword))
    }

    @Operation(summary = "병원 내 사고 건 수 통계 조회(월별)")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{hospitalId}/statistics/month")
    fun getStatisticsByMonth(
        @PathVariable hospitalId: Long,
        @RequestParam(required = false) year: Int,
        @LoginMember member: Member,
    ): ResponseEntity<List<AccidentMonthlyResponse>> {
        validateService.checkMemberHospital(member, hospitalId)
        return ResponseEntity.ok(hospitalService.getStatisticsByMonth(hospitalId, year))
    }

    @Operation(summary = "병원 내 사고 건 수 통계 조회(연도별)")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{hospitalId}/statistics/year")
    fun getStatisticsByYear(
        @PathVariable hospitalId: Long,
        @LoginMember member: Member,
    ): ResponseEntity<List<AccidentYearlyResponse>> {
        validateService.checkMemberHospital(member, hospitalId)
        return ResponseEntity.ok(hospitalService.getStatisticsByYear(hospitalId))
    }
}
