package com.effective.detector.hospital.domain

import org.springframework.data.jpa.repository.JpaRepository

interface HospitalRepository : JpaRepository<Hospital, Long> {

    fun findByTel(tel: String): Hospital?
}
