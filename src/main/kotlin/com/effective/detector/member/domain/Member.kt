package com.effective.detector.member.domain

import com.effective.detector.common.entity.BaseEntity
import com.effective.detector.hospital.domain.Hospital
import jakarta.persistence.*

@Entity
class Member(
    @Column(unique = true)
    private var loginId: String? = null,

    @Column(nullable = false)
    private var loginPassword: String? = null,

    @Column(nullable = false)
    private var name: String? = null,

    @Column(unique = true)
    private var tel: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var memberRole: MemberRole? = null,
) : BaseEntity() {

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val memberHospitals: MutableList<MemberHospital> = mutableListOf()

    fun addHospital(hospital: Hospital) {
        val memberHospital = MemberHospital(this, hospital)
        memberHospitals.add(memberHospital)
    }
}
