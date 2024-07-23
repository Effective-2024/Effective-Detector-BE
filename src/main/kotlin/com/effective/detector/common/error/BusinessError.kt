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
    MEMBER_ROLE_NOT_MATCHED(HttpStatus.CONFLICT, "사용자의 정보가 변경되었습니다."),

    // Hospital
    HOSPITAL_TEL_DUPLICATED(HttpStatus.CONFLICT, "해당 전화번호는 이미 등록되어 있습니다."),

    // Auth
    ID_PASSWORD_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    ROLE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 유저가 ROLE을 가지고 있지 않습니다."),
    MULTIPLE_ROLE_FOUND(HttpStatus.UNAUTHORIZED, "유저가 두 개 이상의 ROLE을 가지고 있습니다."),
    ROLE_TYPE_ERROR(HttpStatus.UNAUTHORIZED, "해당 유저가 정의되지 않은 ROLE을 가지고 있습니다."),
    LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "중복된 아이디 입니다."),
    ;
}
