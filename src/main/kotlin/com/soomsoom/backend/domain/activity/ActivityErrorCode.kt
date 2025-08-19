package com.soomsoom.backend.domain.activity

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class ActivityErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    UNSUPPORTED_ACTIVITY_TYPE(HttpStatus.BAD_REQUEST, "activity.type-unsupported"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "activity.not-found"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
