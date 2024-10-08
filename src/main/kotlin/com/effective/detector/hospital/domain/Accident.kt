package com.effective.detector.hospital.domain

import com.effective.detector.common.entity.BaseEntity
import com.effective.detector.hospital.domain.converter.AccidentTypeConverter
import com.effective.detector.hospital.domain.converter.AgeConverter
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
    var type: AccidentType? = null,

    @Convert(converter = AgeConverter::class)
    @Column(nullable = false)
    var age: AgeType? = null,

    @Column(nullable = false)
    var isProcess: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    val camera: Camera,
) : BaseEntity() {

    fun update(accidentType: AccidentType, ageType: AgeType) {
        this.type = accidentType
        this.age = ageType
    }

    fun process() {
        this.isProcess = true
    }
}
