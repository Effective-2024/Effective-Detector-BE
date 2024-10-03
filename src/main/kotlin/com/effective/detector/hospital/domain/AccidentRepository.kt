package com.effective.detector.hospital.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccidentRepository : JpaRepository<Accident, Long> {
    @Query("SELECT a FROM Accident a WHERE YEAR(a.startTime) = :year")
    fun findAllByYear(@Param("year") year: Int): List<Accident>

    @Query(
        "SELECT a FROM Accident a " +
                "WHERE YEAR(a.startTime) = :year " +
                "AND a.camera.hospital.id = :hospitalId"
    )
    fun findAllByYearAndHospitalId(
        @Param("year") year: Int,
        @Param("hospitalId") hospitalId: Long,
    ): List<Accident>

    @Query("SELECT a FROM Accident a WHERE a.camera.hospital.id = :hospitalId")
    fun findAllByHospitalId(
        @Param("hospitalId") hospitalId: Long,
    ): List<Accident>

    @Query("SELECT a FROM Accident a WHERE a.camera.hospital.id = :hospitalId")
    fun findAllByHospitalId(
        @Param("hospitalId") hospitalId: Long,
        pageable: Pageable,
    ): Page<Accident>
}
