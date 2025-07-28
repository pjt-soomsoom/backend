package com.soomsoom.backend.domain.user

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class UserErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not-found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "user.already-exists"),
    USER_USERNAME_OR_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "user.username-or-password.mismatch"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
