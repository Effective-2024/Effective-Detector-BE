package com.effective.detector.hospital.api.dto

import com.effective.detector.hospital.domain.Hospital

data class HospitalResponse(
    val id: Long,
    val name: String? = null,
    val type: String? = null,
    val address: String? = null,
    val tel: String? = null,
) {
    companion object {
        fun from(
            hospital: Hospital,
        ): HospitalResponse {
            return HospitalResponse(
                id = hospital.id!!,
                name = hospital.name,
                type = hospital.type?.value,
                address = hospital.address,
                tel = hospital.tel,
            )
        }
    }
}
