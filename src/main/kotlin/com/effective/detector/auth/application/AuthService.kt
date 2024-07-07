package com.effective.detector.auth.application

import com.effective.detector.auth.api.dto.SignupRequest
import com.effective.detector.hospital.application.HospitalService
import com.effective.detector.hospital.domain.Hospital
import com.effective.detector.member.application.MemberService
import com.effective.detector.member.domain.Member
import com.effective.detector.member.domain.MemberRole
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberService: MemberService,
    private val passwordEncoder: PasswordEncoder,
    private val hospitalService: HospitalService,
) {

    @Transactional
    fun adminSignup(request: SignupRequest) {
        memberService.validatedLoginId(request.loginId)
        memberService.validatedTel(request.adminTel)
        hospitalService.validatedTel(request.hospitalTel)

        val member = Member(
            loginId = request.loginId,
            loginPassword = passwordEncoder.encode(request.loginPassword),
            name = request.adminName,
            tel = request.adminTel,
            memberRole = MemberRole.ROLE_ADMIN,
        )
        val hospital = Hospital(
            name = request.hospitalName,
            tel = request.hospitalTel,
            address = request.hospitalAddress,
            hospitalType = request.hospitalType,
        )
        member.addHospital(hospital)
        memberService.save(member)
        hospitalService.save(hospital)
    }
}
