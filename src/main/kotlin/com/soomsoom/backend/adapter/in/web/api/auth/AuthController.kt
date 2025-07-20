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

    @PostMapping("/admin/login")
    @ResponseStatus(HttpStatus.OK)
    fun adminLogin(
        @RequestBody request: AdminLoginRequest,
    ): TokenInfo {
        println("controller")
        return adminLoginUseCase.adminLogin(request.toCommand())
    }

    @PostMapping("/admin/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun adminSignUp(
        @RequestBody request: AdminSignUpRequest,
    ): TokenInfo {
        return adminSignUpUserCase.adminSignUp(request.toCommand())
    }
}
