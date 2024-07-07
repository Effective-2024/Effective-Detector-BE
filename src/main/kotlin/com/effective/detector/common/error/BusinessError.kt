package com.effective.detector.common.error

import org.springframework.http.HttpStatus


enum class BusinessError(
    val httpStatus: HttpStatus,
    val message: String,
) {
    // Common
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "ID가 존재하지 않습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    MEMBER_LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다."),
    MEMBER_TEL_DUPLICATED(HttpStatus.CONFLICT, "해당 전화번호는 이미 가입되어 있습니다."),

    // Hospital
    HOSPITAL_TEL_DUPLICATED(HttpStatus.CONFLICT, "해당 전화번호는 이미 등록되어 있습니다."),

    // Auth
    ID_PASSWORD_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    ;
}
