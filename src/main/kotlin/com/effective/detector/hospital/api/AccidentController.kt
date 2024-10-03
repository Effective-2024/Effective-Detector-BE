package com.effective.detector.hospital.api

import com.effective.detector.common.annotation.LoginMember
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.hospital.api.dto.AccidentResponse
import com.effective.detector.hospital.application.AccidentService
import com.effective.detector.member.domain.Member
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        if (!member.isMineHospital(hospitalId)) {
            throw BusinessException(BusinessError.IS_NOT_MY_HOSPITAL)
        }
        return ResponseEntity.ok(accidentService.getAllByHospital(hospitalId, PageRequest.of(page, size)))
    }
}
