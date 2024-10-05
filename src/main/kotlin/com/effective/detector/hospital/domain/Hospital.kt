package com.effective.detector.hospital.domain

import com.effective.detector.common.entity.BaseEntity
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
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

    @OneToMany(mappedBy = "hospital", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val slots: MutableList<Slot> = mutableListOf(),
) : BaseEntity() {

    @OneToMany(mappedBy = "hospital", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val memberHospitals: MutableList<MemberHospital> = mutableListOf()


    fun initSlot() {
        slots.add(Slot(0, this))
        slots.add(Slot(1, this))
        slots.add(Slot(2, this))
        slots.add(Slot(3, this))
        slots.add(Slot(4, this))
    }

    fun findSlot(slot: Int): Slot {
        return slots.find { it.value == slot } ?: throw BusinessException(BusinessError.SLOT_NOT_FOUND)
    }
}
