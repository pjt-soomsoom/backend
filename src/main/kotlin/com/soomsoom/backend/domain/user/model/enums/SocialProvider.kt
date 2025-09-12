package com.soomsoom.backend.domain.user.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.Locale

enum class SocialProvider {
    GOOGLE,
    APPLE,
    ;

    companion object {
        @JvmStatic
        @JsonCreator
        fun from(s: String): SocialProvider {
            return valueOf(s.uppercase(Locale.getDefault()))
        }
    }
}
