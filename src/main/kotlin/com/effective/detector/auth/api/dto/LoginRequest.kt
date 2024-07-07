package com.effective.detector.auth.api.dto

data class LoginRequest(
    val loginId: String,
    val loginPassword: String
)
