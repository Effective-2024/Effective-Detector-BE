package com.effective.detector.hospital.domain

enum class HospitalType(
    private val value: String,
) {
    UNIVERSITY_HOSPITAL("대학병원"),
    NURSING_HOME("요양원"),
    WELFARE_FACILITY("복지시설"),
    GENERAL_HOSPITAL("종합병원"),
    ;
}
