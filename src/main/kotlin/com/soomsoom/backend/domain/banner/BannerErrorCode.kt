package com.soomsoom.backend.domain.banner

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class BannerErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "banner.not-found"),
    INVALID_DISPLAY_ORDER(HttpStatus.BAD_REQUEST, "banner.invalid-display-order"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
