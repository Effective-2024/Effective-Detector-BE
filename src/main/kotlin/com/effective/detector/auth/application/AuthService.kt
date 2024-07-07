package com.effective.detector.auth.application

import com.effective.detector.auth.adapter.AuthenticationAdapter
import com.effective.detector.auth.api.dto.LoginRequest
import com.effective.detector.auth.api.dto.MemberMeResponse
import com.effective.detector.auth.api.dto.SignupRequest
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.common.helper.AuthorizationHelper
import com.effective.detector.hospital.application.HospitalService
import com.effective.detector.hospital.domain.Hospital
import com.effective.detector.member.application.MemberService
import com.effective.detector.member.domain.Member
import com.effective.detector.member.domain.MemberRole
import com.effective.detector.member.domain.MemberStatus
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val memberService: MemberService,
    private val passwordEncoder: PasswordEncoder,
    private val hospitalService: HospitalService,
    private val authenticationManager: AuthenticationManager,
    private val cookieService: CookieService,
    private val authorizationHelper: AuthorizationHelper,
) {

    @Transactional
    fun signup(request: SignupRequest) {
        memberService.validatedLoginId(request.loginId)
        memberService.validatedTel(request.adminTel)
        hospitalService.validatedTel(request.hospitalTel)

        val member = Member(
            loginId = request.loginId,
            loginPassword = passwordEncoder.encode(request.loginPassword),
            name = request.adminName,
            tel = request.adminTel,
            memberRole = MemberRole.ROLE_ADMIN,
            memberStatus = MemberStatus.ACTIVE,
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

    fun login(response: HttpServletResponse, loginDto: LoginRequest): MemberMeResponse {
        val authentication: Authentication
        try {
            authentication = this.getAuthenticationFromIdPassword(
                loginDto.loginId, loginDto.loginPassword
            )
        } catch (exception: AuthenticationException) {
            throw BusinessException(BusinessError.ID_PASSWORD_AUTH_FAILED)
        }
        val adapter: AuthenticationAdapter = authentication.principal as AuthenticationAdapter
        cookieService.authenticate(adapter.getId(), adapter.getMemberRole(), response)
        return memberService.getMemberMeById(adapter.getId())
    }

    private fun getAuthenticationFromIdPassword(loginId: String, loginPassword: String): Authentication {
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginId, loginPassword))
    }

    fun getMemberInfo(response: HttpServletResponse): MemberMeResponse? {
        val id: Long = authorizationHelper.getMyId().toLong()
        val member: Member = memberService.getById(id)
        if (authorizationHelper.getMyRole() != member.memberRole) {
            this.logout(response)
            throw BusinessException(BusinessError.MEMBER_ROLE_NOT_MATCHED)
        }
        return memberService.getMemberMeById(id)
    }

    fun logout(response: HttpServletResponse) {
        response.addCookie(cookieService.deleteAccessTokenCookie())
    }
}
