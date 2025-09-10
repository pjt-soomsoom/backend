package com.soomsoom.backend.domain.user

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class UserErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "user.not-found"),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "user.already-exists"),
    USERNAME_OR_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "user.username-or-password.mismatch"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "user.access-denied"),

    NOT_ENOUGH_POINTS(HttpStatus.BAD_REQUEST, "user.not-enough-points"),
    ITEM_ALREADY_OWNED(HttpStatus.CONFLICT, "user.item-already-owned"),
    ITEM_NOT_OWNED(HttpStatus.BAD_REQUEST, "user.item-not-owned"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
