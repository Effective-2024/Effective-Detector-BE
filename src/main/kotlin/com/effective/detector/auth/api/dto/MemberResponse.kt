package com.effective.detector.auth.api.dto

import com.effective.detector.hospital.api.dto.HospitalResponse
import com.effective.detector.member.domain.MemberRole

data class MemberResponse(
    val id: Long? = null,
    val loginId: String? = null,
    val name: String? = null,
    val memberRole: MemberRole? = null,
    val tel: String? = null,
    val hospital: HospitalResponse? = null,
)
