package com.effective.detector.member.application

import com.effective.detector.auth.api.dto.MemberMeResponse
import com.effective.detector.common.error.BusinessError
import com.effective.detector.common.error.BusinessException
import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.member.domain.Member
import com.effective.detector.member.domain.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
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

    fun getMemberMeById(id: Long?): MemberMeResponse {
        val member = memberRepository.findById(id!!).orElseThrow {
            BusinessException(BusinessError.ID_NOT_FOUND)
        }
        return MemberMeResponse(
            id = member.id,
            name = member.name,
            memberRole = member.memberRole,
        )
    }

    fun getById(id: Long): Member {
        return memberRepository.findByIdOrThrow(id)
    }
}
