package com.effective.detector.hospital.api.dto.response

import com.effective.detector.hospital.domain.Accident

data class UnprocessAccidentResponse(
    val id: Long,
    val videoUrl: String,
    val startDate: String,
    val camera: CameraResponse,
) {
    companion object {
        fun from(accident: Accident): UnprocessAccidentResponse {
            return UnprocessAccidentResponse(
                id = accident.id!!,
                videoUrl = accident.videoUrl,
                startDate = accident.startTime.toString(),
                camera = CameraResponse.from(accident.camera)!!,
            )
        }
    }
}
