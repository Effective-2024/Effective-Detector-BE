package com.effective.detector.hospital.api.dto

data class MonitorChangeRequest(
    val slot: Int,
    val cameraId: Long,
)
