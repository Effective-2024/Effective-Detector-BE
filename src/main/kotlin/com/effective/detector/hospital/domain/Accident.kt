package com.effective.detector.hospital.domain

import com.effective.detector.common.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Accident(
    @Column(nullable = false)
    val videoUrl: String,

    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    val endTime: LocalDateTime,

    @Convert(converter = AccidentTypeConverter::class)
    @Column(nullable = false)
    val type: AccidentType? = AccidentType.ETC,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    val camera: Camera,
) : BaseEntity()
