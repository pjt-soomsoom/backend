package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.SocialAuthenticationCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AuthenticateWithSocialUseCase
import com.soomsoom.backend.application.port.out.auth.SocialProfileInfo
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.auth.VerifySocialTokenPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SocialAuthenticationService(
    private val userPort: UserPort,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val verifySocialTokenPort: VerifySocialTokenPort,
    private val tokenServiceLogic: TokenServiceLogic,
    private val eventPublisher: ApplicationEventPublisher,
) : AuthenticateWithSocialUseCase {
    override fun authenticate(command: SocialAuthenticationCommand): TokenInfo {
        val socialProfile = verifySocialTokenPort.verify(command.provider, command.providerToken)
        val finalUser = processUserAuthentication(command.deviceId, socialProfile)

        val sessionRole = finalUser.role
        val account = finalUser.account
        val deviceId = when (account) {
            is Account.Anonymous -> account.deviceId
            is Account.Social -> account.deviceId
            else -> null
        }
        val principal = CustomUserDetails.of(user = finalUser, deviceId = deviceId, sessionRole = sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val tokenResult = tokenGeneratorPort.generateToken(authentication)
        tokenServiceLogic.manageRefreshToken(finalUser.id!!, tokenResult)

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.USER_AUTHENTICATED,
                payload = UserAuthenticatedPayload(userId = finalUser.id!!)
            )
        )

        return TokenInfo(tokenResult.accessToken, tokenResult.refreshToken)
    }

    private fun processUserAuthentication(deviceId: String, socialProfile: SocialProfileInfo): User {
        // 이미 소셜 계정으로 가입된 사용자인지 확인 (재로그인)
        userPort.findBySocialId(socialProfile.provider, socialProfile.socialId)?. let {
            return it
        }

        // 기존 익명 계정이 있는지 확인 (계정 연동)
        val deviceUser = userPort.findByDeviceId(deviceId)
        if (deviceUser != null) {
            // 이 기기의 계정이 '순수한 익명 계정'인지 확인
            if (deviceUser.account is Account.Anonymous) {
                // '익명'이 맞다면, 안전하게 계정을 연동
                deviceUser.linkSocialAccount(socialProfile.provider, socialProfile.socialId)
                return userPort.save(deviceUser)
            } else {
                // '익명'이 아니라면(이미 다른 소셜 계정과 연동됨), 연동하지 않고 에러를 발생
                throw SoomSoomException(UserErrorCode.DEVICE_ALREADY_LINKED)
            }
        } else {
            // deviceId로 가입된 계정이 없다면 에러 반환
            throw SoomSoomException(UserErrorCode.DEVICE_ID_NOT_FOUND)
        }
    }
}
