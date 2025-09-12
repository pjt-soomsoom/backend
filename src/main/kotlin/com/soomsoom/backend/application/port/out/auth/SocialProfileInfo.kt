package com.soomsoom.backend.application.port.out.auth

import com.soomsoom.backend.domain.user.model.enums.SocialProvider

data class SocialProfileInfo(
    val provider: SocialProvider,
    val socialId: String,
    val email: String?
)
