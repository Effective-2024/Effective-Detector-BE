package com.effective.detector.hospital.domain

enum class AccidentType(
    val value: String,
) {
    SLIP("미끄러짐"),
    PATIENT_DISPUTE("환자 간 다툼"),
    FACILITY_DEFECT("시설 不실"),
    MEDICAL_STAFF_ERROR("의료진 부주의"),
    OTHER("기타"),
    MALFUNCTION("오작동"),
    ;
}
