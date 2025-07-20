package com.soomsoom.backend.adapter.`in`.web.api.auth

import com.soomsoom.backend.adapter.`in`.web.api.auth.request.AdminLoginRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.AdminSignUpRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.toCommand
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminLoginUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminSignUpUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val adminLoginUseCase: AdminLoginUseCase,
    private val adminSignUpUserCase: AdminSignUpUseCase,
) {

    /**
     * 관리자 로그인을 처리하고 인증 토큰 정보를 반환합니다.
     *
     * @param request 관리자 로그인 요청 데이터.
     * @return 인증에 성공한 관리자의 토큰 정보.
     */
    @PostMapping("/admin/login")
    @ResponseStatus(HttpStatus.OK)
    fun adminLogin(
        @RequestBody request: AdminLoginRequest,
    ): TokenInfo {
        println("controller")
        return adminLoginUseCase.adminLogin(request.toCommand())
    }

    /**
     * 관리자 회원가입 요청을 처리하고 인증 토큰 정보를 반환합니다.
     *
     * @param request 관리자 회원가입 요청 데이터.
     * @return 생성된 관리자 계정의 인증 토큰 정보.
     */
    @PostMapping("/admin/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun adminSignUp(
        @RequestBody request: AdminSignUpRequest,
    ): TokenInfo {
        return adminSignUpUserCase.adminSignUp(request.toCommand())
    }
}
