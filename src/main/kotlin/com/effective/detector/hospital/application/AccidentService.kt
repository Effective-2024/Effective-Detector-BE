package com.effective.detector.hospital.application

import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.hospital.api.dto.response.*
import com.effective.detector.hospital.domain.Accident
import com.effective.detector.hospital.domain.AccidentRepository
import com.effective.detector.hospital.domain.AccidentType
import com.effective.detector.hospital.domain.AgeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

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
        return accidentRepository.findDistinctYears()
    }

    @Transactional
    fun update(accidentId: Long, typeId: Long, ageId: Long) {
        val accident = accidentRepository.findByIdOrThrow(accidentId)
        accident.update(AccidentType.from(typeId), AgeType.from(ageId))
    }

    fun getUnprocessAccident(hospitalId: Long): List<AccidentResponse> {
        return accidentRepository.findAllByHospitalIdAndUnprocess(hospitalId).map { AccidentResponse.from(it) }
    }

    fun getYearByExistAccidentHospital(hospitalId: Long): List<Int> {
        return accidentRepository.findDistinctYearsByHospitalId(hospitalId)
    }

    fun getPerformance(): AllPerformanceStatisticResponse {
        val currentYear = LocalDate.now().year
        val lastYear = currentYear.dec()
        val totalAccidentCount = accidentRepository.getTotalAccidentCountForYear(currentYear)
        val lastYearAccidentCount = accidentRepository.getTotalAccidentCountForYear(lastYear)

        val primaryReason =
            accidentRepository.getPrimaryReasonForAccidents(currentYear) ?: PrimaryReasonResponse(0, "미상")

        val increaseRateByLastYear = if (lastYearAccidentCount != 0L) {
            ((totalAccidentCount - lastYearAccidentCount).toDouble() / lastYearAccidentCount) * 100
        } else {
            100.0 // 전년도 사고가 없을 경우 100% 증가로 간주
        }

        val malfunctionAccidentCount = accidentRepository.getMalfunctionAccidentCount(currentYear)
        val detectionAccuracy = if (totalAccidentCount > 0) {
            (malfunctionAccidentCount.toDouble() / totalAccidentCount) * 100
        } else {
            0.0
        }

        val mostAccidentsData = accidentRepository.getMostAccidentsOccurredByMonthAndYear(currentYear)
        val mostAccidentsOrccuredMonth = (mostAccidentsData.firstOrNull()?.get(1) ?: 1)
        val mostAccidentsOrccuredYear = (mostAccidentsData.firstOrNull()?.get(0) ?: currentYear)

        return AllPerformanceStatisticResponse(
            totalAccidentCount = totalAccidentCount,
            primaryReason = primaryReason,
            increaseRateByLastYear = increaseRateByLastYear,
            detectionAccuracy = detectionAccuracy,
            mostAccidentsOrccuredMonth = mostAccidentsOrccuredMonth,
            mostAccidentsOrccuredYear = mostAccidentsOrccuredYear
        )
    }

    fun getPerformanceByHospital(hospitalId: Long): HospitalPerformanceStatisticResponse {
        val currentYear = LocalDate.now().year
        val lastYear = currentYear.dec()
        val totalAccidentCount = accidentRepository.getTotalAccidentCountByHospitalId(hospitalId, currentYear)
        val lastYearAccidentCount = accidentRepository.getTotalAccidentCountByHospitalId(hospitalId, lastYear)

        val primaryReason =
            accidentRepository.getPrimaryReasonForAccidentsByHospitalId(hospitalId, currentYear)
                ?: PrimaryReasonResponse(0, "미상")

        val increaseRateByLastYear = if (lastYearAccidentCount != 0L) {
            ((totalAccidentCount - lastYearAccidentCount).toDouble() / lastYearAccidentCount) * 100
        } else {
            100.0 // 전년도 사고가 없을 경우 100% 증가로 간주
        }

        val malfunctionAccidentCount =
            accidentRepository.getMalfunctionAccidentCountByHospitalId(hospitalId, currentYear)
        val detectionAccuracy = if (totalAccidentCount > 0) {
            (malfunctionAccidentCount.toDouble() / totalAccidentCount) * 100
        } else {
            0.0
        }

        val mostAccidentsData =
            accidentRepository.getMostAccidentsOccurredByMonthAndYearByHospitalId(hospitalId, currentYear)
        val mostAccidentsOrccuredMonth = (mostAccidentsData.firstOrNull()?.get(1) ?: 1) as Int

        val allAccidentCount = accidentRepository.getTotalAccidentCountForYear(currentYear)
        val increaseRateByAverage = if (allAccidentCount > 0) {
            (totalAccidentCount.toDouble() / allAccidentCount) * 100
        } else {
            0.0
        }

        return HospitalPerformanceStatisticResponse(
            totalAccidentCount = totalAccidentCount,
            primaryReason = primaryReason,
            increaseRateByLastYear = increaseRateByLastYear,
            detectionAccuracy = detectionAccuracy,
            mostAccidentsOrccuredMonth = mostAccidentsOrccuredMonth,
            increaseRateByAverage = increaseRateByAverage,
        )
    }

    fun processAccident(accidentId: Long) {
        val accident = accidentRepository.findByIdOrThrow(accidentId)
        accident.process()
    }
}
