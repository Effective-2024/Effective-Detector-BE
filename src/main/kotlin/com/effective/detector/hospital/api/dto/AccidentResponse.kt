package com.effective.detector.hospital.api.dto

import com.effective.detector.hospital.domain.Accident

data class AccidentResponse(
    val id: Long,
) {
    companion object {
        fun from(it: Accident?): AccidentResponse {
            return AccidentResponse(
                id = it!!.id!!,
            )
        }
    }
}
