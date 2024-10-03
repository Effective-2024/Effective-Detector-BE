package com.effective.detector.hospital.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccidentRepository : JpaRepository<Accident, Long> {
    @Query("SELECT a FROM Accident a WHERE YEAR(a.startTime) = :year")
    fun findAllByYear(@Param("year") year: Int): List<Accident>
}
