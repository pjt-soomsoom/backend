package com.soomsoom.backend.application.port.`in`.auth.command

import com.soomsoom.backend.domain.user.model.enums.SocialProvider

data class SocialAuthenticationCommand(
    val provider: SocialProvider,
    val providerToken: String,
    val deviceId: String,
)
