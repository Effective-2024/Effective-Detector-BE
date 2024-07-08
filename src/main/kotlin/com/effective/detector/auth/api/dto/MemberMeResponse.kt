package com.effective.detector.auth.api.dto

import com.effective.detector.member.domain.MemberRole

data class MemberMeResponse(
    val id: Long? = null,
    val name: String? = null,
    val memberRole: MemberRole? = null,
)
