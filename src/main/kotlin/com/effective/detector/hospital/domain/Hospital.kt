package com.effective.detector.hospital.domain

import com.effective.detector.common.entity.BaseEntity
import com.effective.detector.member.domain.MemberHospital
import jakarta.persistence.*

@Entity
class Hospital(
    @Column(nullable = false)
    private val name: String? = null,

    @Column(unique = true)
    private var tel: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var hospitalType: HospitalType? = null,

    @Column(nullable = false)
    private val address: String? = null,
) : BaseEntity() {

    @OneToMany(mappedBy = "hospital", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val memberHospitals: MutableList<MemberHospital> = mutableListOf()
}
