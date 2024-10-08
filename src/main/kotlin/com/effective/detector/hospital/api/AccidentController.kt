package com.effective.detector.hospital.api

import com.effective.detector.common.annotation.LoginMember
import com.effective.detector.hospital.api.dto.request.AccidentChangeRequest
import com.effective.detector.hospital.api.dto.response.AccidentResponse
import com.effective.detector.hospital.application.AccidentService
import com.effective.detector.hospital.application.ValidateService
import com.effective.detector.member.domain.Member
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "[Accident] 사고", description = "사고 관련 기능")
@RestController
@RequestMapping("/accidents")
class AccidentController(
    private val accidentService: AccidentService,
    private val validateService: ValidateService,
) {

    @Operation(summary = "전국 사고 목록 조회")
    @PreAuthorize("permitAll()")
    @GetMapping
    fun getAll(
        @RequestParam(required = false) pageNumber: Int?,
        @RequestParam(required = false) pageSize: Int?,
    ): ResponseEntity<Page<AccidentResponse>> {
        val page = pageNumber ?: 0
        val size = pageSize ?: 10
        return ResponseEntity.ok(accidentService.getAll(PageRequest.of(page, size)))
    }

    @Operation(summary = "병원 내 사고 목록 조회")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/hospitals/{hospitalId}")
    fun getAllByHospital(
        @PathVariable hospitalId: Long,
        @RequestParam(required = false) pageNumber: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @LoginMember member: Member,
    ): ResponseEntity<Page<AccidentResponse>> {
        val page = pageNumber ?: 0
        val size = pageSize ?: 10
        validateService.checkMemberHospital(member, hospitalId)
        return ResponseEntity.ok(accidentService.getAllByHospital(hospitalId, PageRequest.of(page, size)))
    }

    @Operation(summary = "사고 정보 수정")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PatchMapping("/{accidentId}")
    fun update(
        @PathVariable accidentId: Long,
        @RequestBody request: AccidentChangeRequest,
        @LoginMember member: Member,
    ): ResponseEntity<Void> {
        validateService.checkMemberHospitalAccident(member, accidentId)
        accidentService.update(accidentId, request.type, request.age)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "처리되지 않은 사고 정보 조회")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/hospitals/{hospitalId}/unprocessed")
    fun getUnprocessAccident(
        @PathVariable hospitalId: Long,
        @LoginMember member: Member,
    ): ResponseEntity<List<AccidentResponse>> {
        validateService.checkMemberHospital(member, hospitalId)
        return ResponseEntity.ok(accidentService.getUnprocessAccident(hospitalId))
    }

    @Operation(summary = "사고 처리")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PatchMapping("/{accidentId}/process")
    fun processAccident(
        @PathVariable accidentId: Long,
        @LoginMember member: Member,
    ): ResponseEntity<Void> {
        validateService.checkMemberHospitalAccident(member, accidentId)
        accidentService.processAccident(accidentId)
        return ResponseEntity.noContent().build()
    }
}

