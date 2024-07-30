package com.effective.detector.hospital.application

import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.hospital.api.dto.HospitalResponse
import com.effective.detector.hospital.domain.Hospital
import com.effective.detector.hospital.domain.HospitalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class HospitalService(
    private val hospitalRepository: HospitalRepository,
) {

    fun findHospital(id: Long): Hospital {
        return hospitalRepository.findByIdOrThrow(id)
    }

    fun findHospitalsByKeyword(keyword: String?): List<HospitalResponse> {
        return if (keyword.isNullOrBlank())
            hospitalRepository.findAll().map { HospitalResponse.from(it) }
        else
            hospitalRepository.findByNameContaining(keyword).map { HospitalResponse.from(it) }
    }
}
