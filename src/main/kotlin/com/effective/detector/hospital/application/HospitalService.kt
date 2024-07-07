package com.effective.detector.hospital.application

import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.hospital.domain.Hospital
import com.effective.detector.hospital.domain.HospitalRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class HospitalService(
    private val hospitalRepository: HospitalRepository,
) {
    fun validatedTel(hospitalTel: String) {
        hospitalRepository.findByTel(hospitalTel)?.let {
            throw BusinessException(BusinessError.HOSPITAL_TEL_DUPLICATED)
        }
    }

    @Transactional
    fun save(hospital: Hospital) {
        hospitalRepository.save(hospital)
    }
}
