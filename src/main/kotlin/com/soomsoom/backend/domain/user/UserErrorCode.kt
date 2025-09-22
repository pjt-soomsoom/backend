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
    DEVICE_ALREADY_LINKED(HttpStatus.CONFLICT, "user.device-already-linked"),

    NOT_ENOUGH_POINTS(HttpStatus.BAD_REQUEST, "user.not-enough-points"),
    ITEM_ALREADY_OWNED(HttpStatus.CONFLICT, "user.item-already-owned"),
    ITEM_NOT_OWNED(HttpStatus.BAD_REQUEST, "user.item-not-owned"),

    // AUTH
    PROVIDER_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "auth.provider-token-invalid"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "auth.refresh-token.not_found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "auth.refresh-token.expired"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "auth.access-token.expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "auth.token.invalid"),
    DEVICE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "auth.device-id.not-found"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
