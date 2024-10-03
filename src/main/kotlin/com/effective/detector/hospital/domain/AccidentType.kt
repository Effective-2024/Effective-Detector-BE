package com.effective.detector.hospital.domain

enum class AccidentType(
    val value: String,
) {
    SLIPPIING("미끄러짐"),
    FIGHTING("환자 간 다툼"),
    POOR_FACILITIES("시설 부실"),
    CARELESSNESS("의료진 부주의"),
    ETC("기타"),
    MALFUNCTION("오작동"),
    ;
}
