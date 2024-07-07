package com.effective.detector.auth.api

import com.effective.detector.auth.api.dto.SignupRequest
import com.effective.detector.auth.application.AuthService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PreAuthorize("permitAll()")
    @PostMapping("/admin-signup")
    fun adminSignup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Void> {
        authService.adminSignup(request)
        return ResponseEntity.ok().build()
    }
}
