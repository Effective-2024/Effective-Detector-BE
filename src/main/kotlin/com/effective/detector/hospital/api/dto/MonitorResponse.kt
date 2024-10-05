package com.effective.detector.hospital.api.dto

import com.effective.detector.hospital.domain.Camera

data class MonitorResponse(
    val id: Long,
    val content: String,
) {
    companion object {
        fun from(camera: Camera?): MonitorResponse? {
            if (camera == null) {
                return null
            }
            return MonitorResponse(
                id = camera.id!!,
                content = camera.name,
            )
        }
    }
}
