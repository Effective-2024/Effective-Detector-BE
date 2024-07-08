package com.effective.detector.auth.api

import com.effective.detector.auth.api.dto.LoginRequest
import com.effective.detector.auth.api.dto.MemberMeResponse
import com.effective.detector.auth.api.dto.SignupRequest
import com.effective.detector.auth.application.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "[Auth] 로그인 및 회원가입", description = "인증 관련 기능")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(summary = "회원가입 & 병원 등록")
    @PreAuthorize("permitAll()")
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Void> {
        authService.signup(request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "로그인")
    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    fun login(
        response: HttpServletResponse,
        @RequestBody @Valid loginDto: LoginRequest,
    ): ResponseEntity<MemberMeResponse> {
        return ResponseEntity.ok(authService.login(response, loginDto))
    }

    @Operation(summary = "로그아웃")
    @PreAuthorize("permitAll()")
    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        authService.logout(response)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "내 정보 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/members/me")
    fun getMemberInfo(
        response: HttpServletResponse,
    ): ResponseEntity<MemberMeResponse> {
        return ResponseEntity.ok(authService.getMemberInfo(response))
    }
}
