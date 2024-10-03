package com.effective.detector.hospital.application

import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.hospital.api.dto.AccidentMonthlyResponse
import com.effective.detector.hospital.api.dto.AccidentResponse
import com.effective.detector.hospital.api.dto.AccidentYearlyResponse
import com.effective.detector.hospital.domain.Accident
import com.effective.detector.hospital.domain.AccidentRepository
import com.effective.detector.hospital.domain.AccidentType
import com.effective.detector.hospital.domain.AgeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AccidentService(
    private val accidentRepository: AccidentRepository,
) {

    fun getStatisticsByMonth(year: Int): List<AccidentMonthlyResponse> {
        val accidents: List<Accident> = accidentRepository.findAllByYear(year)
        return accidents.groupBy { it.startTime.month }
            .map { (month, accidentsInMonth) ->
                val typeCounts = accidentsInMonth.groupingBy { it.type ?: AccidentType.ETC }.eachCount()
                AccidentMonthlyResponse(
                    month = "${month.value}월",
                    slipping = typeCounts[AccidentType.SLIPPIING] ?: 0,
                    fighting = typeCounts[AccidentType.FIGHTING] ?: 0,
                    poorFacilities = typeCounts[AccidentType.POOR_FACILITIES] ?: 0,
                    carelessness = typeCounts[AccidentType.CARELESSNESS] ?: 0,
                    malfunction = typeCounts[AccidentType.MALFUNCTION] ?: 0,
                    etc = typeCounts[AccidentType.ETC] ?: 0,
                    total = accidentsInMonth.size,
                )
            }
    }

    fun getStatisticsByYear(): List<AccidentYearlyResponse> {
        val accidents = accidentRepository.findAll()
        return accidents.groupBy { it.startTime.year }
            .map { (year, accidentsInYear) ->
                val typeCounts = accidentsInYear.groupingBy { it.type ?: AccidentType.ETC }.eachCount()
                AccidentYearlyResponse(
                    year = year,
                    slipping = typeCounts[AccidentType.SLIPPIING] ?: 0,
                    fighting = typeCounts[AccidentType.FIGHTING] ?: 0,
                    poorFacilities = typeCounts[AccidentType.POOR_FACILITIES] ?: 0,
                    carelessness = typeCounts[AccidentType.CARELESSNESS] ?: 0,
                    malfunction = typeCounts[AccidentType.MALFUNCTION] ?: 0,
                    etc = typeCounts[AccidentType.ETC] ?: 0,
                    total = accidentsInYear.size
                )
            }
    }

    fun getAll(pageable: Pageable): Page<AccidentResponse> {
        return accidentRepository.findAll(pageable).map { AccidentResponse.from(it) }
    }

    fun getAllByHospital(hospitalId: Long, pageable: Pageable): Page<AccidentResponse>? {
        return accidentRepository.findAllByHospitalId(hospitalId, pageable)
            .map { AccidentResponse.from(it) }
    }

    fun getYearByExistAccident(): List<Int> {
        return listOf()
    }

    @Transactional
    fun update(accidentId: Long, typeId: Long, ageId: Long) {
        val accident = accidentRepository.findByIdOrThrow(accidentId)
        accident.update(AccidentType.from(typeId), AgeType.from(ageId))
    }

    fun getUnprocessAccident(hospitalId: Long): List<AccidentResponse> {
        return accidentRepository.findAllByHospitalIdAndUnprocess(hospitalId).map { AccidentResponse.from(it) }
    }
}
