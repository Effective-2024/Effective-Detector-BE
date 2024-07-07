package com.effective.detector.auth.api

import com.effective.detector.auth.api.dto.SignupRequest
import com.effective.detector.auth.application.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[Auth] 로그인 및 회원가입", description = "인증 관련 기능")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(summary = "관리자 회원가입 & 병원 등록")
    @PreAuthorize("permitAll()")
    @PostMapping("/admin-signup")
    fun adminSignup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Void> {
        authService.adminSignup(request)
        return ResponseEntity.ok().build()
    }
}
