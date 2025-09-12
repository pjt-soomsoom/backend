package com.soomsoom.backend.domain.user.model

import com.soomsoom.backend.domain.user.model.enums.SocialProvider

sealed class Account {
    data class Anonymous(val deviceId: String) : Account()
    data class Social(val socialProvider: SocialProvider, val socialId: String, val deviceId: String) : Account()

    // 관리자
    data class IdPassword(val username: String, val password: String) : Account()
}
