package com.effective.detector.auth.application

import com.effective.detector.member.domain.MemberRole
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CookieService(
    private val jwtService: JwtService,
) {
    @Value("\${cookie.domain}")
    private lateinit var domain: String

    @Value("\${spring.profiles.active}")
    private lateinit var activeProfile: String

    fun authenticate(id: Long?, memberRole: MemberRole, response: HttpServletResponse) {
        val accessToken: String = jwtService.generateAccessToken(id, memberRole)
        response.addCookie(this.makeAccessTokenCookie(accessToken))
    }

    private fun makeAccessTokenCookie(token: String): Cookie {
        return this.makeCookie("access_token", token, 604800)
    }

    private fun makeCookie(key: String, value: String, maxAge: Int): Cookie {
        val cookie = Cookie(key, value)
        if (activeProfile == "prod") {
            cookie.secure = true
        }
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.domain = domain
        cookie.maxAge = maxAge
        return cookie
    }
}
