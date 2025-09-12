package com.soomsoom.backend.adapter.`in`.web.api.auth

import com.soomsoom.backend.adapter.`in`.web.api.auth.request.AdminLoginRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.AdminSignUpRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.DeviceAuthenticationRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.LogoutRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.RefreshTokenRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.SocialAuthenticationRequest
import com.soomsoom.backend.adapter.`in`.web.api.auth.request.toCommand
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AdminLoginUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AdminSignUpUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AuthenticateWithDeviceUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AuthenticateWithSocialUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.LogoutUseCase
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.RefreshTokenUseCase
import jakarta.validation.Valid
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
    private val adminSignUpUseCase: AdminSignUpUseCase,
    private val authenticateWithSocialUseCase: AuthenticateWithSocialUseCase,
    private val authenticateWithDeviceUseCase: AuthenticateWithDeviceUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) {

    @PostMapping("/admin/login")
    @ResponseStatus(HttpStatus.OK)
    fun adminLogin(
        @Valid @RequestBody
        request: AdminLoginRequest,
    ): TokenInfo {
        return adminLoginUseCase.adminLogin(request.toCommand())
    }

    @PostMapping("/admin/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun adminSignUp(
        @Valid @RequestBody
        request: AdminSignUpRequest,
    ): TokenInfo {
        return adminSignUpUseCase.adminSignUp(request.toCommand())
    }

    @PostMapping("/device")
    @ResponseStatus(HttpStatus.OK)
    fun authenticateWithDevice(
        @Valid @RequestBody
        request: DeviceAuthenticationRequest,
    ): TokenInfo {
        return authenticateWithDeviceUseCase.authenticate(request.toCommand())
    }

    @PostMapping("/social")
    @ResponseStatus(HttpStatus.OK)
    fun authenticateWithSocial(
        @Valid @RequestBody
        request: SocialAuthenticationRequest,
    ): TokenInfo {
        return authenticateWithSocialUseCase.authenticate(request.toCommand())
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(
        @Valid @RequestBody
        request: RefreshTokenRequest,
    ): TokenInfo {
        return refreshTokenUseCase.refreshToken(request.refreshToken)
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        @Valid @RequestBody
        request: LogoutRequest,
    ) {
        logoutUseCase.logout(request.refreshToken)
    }
}
