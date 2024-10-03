package com.effective.detector.hospital.domain

import com.effective.detector.common.entity.BaseEntity
import com.effective.detector.hospital.domain.HospitalType.HOSPITAL
import com.effective.detector.hospital.domain.converter.HospitalTypeConverter
import com.effective.detector.member.domain.MemberHospital
import jakarta.persistence.*

@Entity
class Hospital(
    @Column(nullable = false)
    val name: String? = null,

    @Column(unique = true)
    var tel: String? = null,

    @Convert(converter = HospitalTypeConverter::class)
    @Column(nullable = false)
    var type: HospitalType? = HOSPITAL,

    @Column(nullable = false)
    val address: String? = null,

    @Column(nullable = false)
    private val medicalPersonCount: Int? = 0,

    @Column(nullable = false)
    private val roomCount: Int? = 0,

    @Column(nullable = false)
    private val bedCount: Int? = 0,

    @Column(nullable = false)
    private val area: Double? = 0.0,

    @Column(nullable = false)
    private val department: String? = null,

    @OneToMany(mappedBy = "hospital", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val cameras: List<Camera> = mutableListOf(),
) : BaseEntity() {

    @OneToMany(mappedBy = "hospital", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val memberHospitals: MutableList<MemberHospital> = mutableListOf()
}
