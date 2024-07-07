package com.effective.detector.member.application

import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.member.domain.Member
import com.effective.detector.member.domain.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun validatedLoginId(loginId: String) {
        memberRepository.findByLoginId(loginId)?.let {
            throw BusinessException(BusinessError.MEMBER_LOGIN_ID_DUPLICATED)
        }
    }

    fun validatedTel(adminTel: String) {
        memberRepository.findByTel(adminTel)?.let {
            throw BusinessException(BusinessError.MEMBER_TEL_DUPLICATED)
        }
    }

    @Transactional
    fun save(member: Member) {
        memberRepository.save(member)
    }
}
