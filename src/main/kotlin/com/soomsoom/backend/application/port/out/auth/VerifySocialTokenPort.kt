package com.soomsoom.backend.application.port.out.auth

import com.soomsoom.backend.domain.user.model.enums.SocialProvider

interface VerifySocialTokenPort {
    fun verify(provider: SocialProvider, token: String): SocialProfileInfo
}
