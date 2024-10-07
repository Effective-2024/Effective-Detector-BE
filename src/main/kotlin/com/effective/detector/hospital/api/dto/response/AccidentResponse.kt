package com.effective.detector.hospital.api.dto.response

import com.effective.detector.hospital.domain.Accident
import java.time.LocalDate
import java.time.LocalDateTime

data class AccidentResponse(
    val id: Long,
    val videoUrl: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val date: LocalDate,
    val type: AccidentTypeResponse,
    val age: AgeResponse,
) {
    companion object {
        fun from(accident: Accident): AccidentResponse {
            return AccidentResponse(
                id = accident.id!!,
                videoUrl = accident.videoUrl,
                startTime = accident.startTime,
                endTime = accident.endTime,
                date = accident.startTime.toLocalDate(),
                type = AccidentTypeResponse(
                    id = accident.type!!.id,
                    name = accident.type!!.value,
                ),
                age = AgeResponse(
                    id = accident.age!!.id,
                    name = accident.age!!.value,
                ),
            )
        }
    }
}

data class AccidentTypeResponse(
    val id: Long,
    val name: String,
)

data class AgeResponse(
    val id: Long,
    val name: String,
)
