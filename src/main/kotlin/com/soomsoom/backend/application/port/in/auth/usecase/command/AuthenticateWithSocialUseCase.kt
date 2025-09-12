package com.soomsoom.backend.application.port.`in`.auth.usecase.command

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.SocialAuthenticationCommand

interface AuthenticateWithSocialUseCase {
    fun authenticate(command: SocialAuthenticationCommand): TokenInfo
}
