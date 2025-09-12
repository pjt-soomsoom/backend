package com.soomsoom.backend.adapter.`in`.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import com.soomsoom.backend.common.exception.ErrorResponse
import com.soomsoom.backend.domain.user.UserErrorCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val messageSource: MessageSource,
) : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            resolveToken(request)?.let { token ->
                jwtTokenProvider.validateToken(token)
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            // Access Token 만료 시, 명확한 에러 응답을 직접 생성
            setErrorResponse(response, UserErrorCode.ACCESS_TOKEN_EXPIRED)
        } catch (e: JwtException) {
            // 그 외 JWT 관련 에러 발생 시, 유효하지 않은 토큰으로 응답
            setErrorResponse(response, UserErrorCode.INVALID_TOKEN)
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)
    }

    private fun setErrorResponse(response: HttpServletResponse, errorCode: UserErrorCode) {
        response.status = errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponse.of(errorCode, messageSource)

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
