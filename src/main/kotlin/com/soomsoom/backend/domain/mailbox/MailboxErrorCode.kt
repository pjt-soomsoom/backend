package com.soomsoom.backend.domain.mailbox

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class MailboxErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    ANNOUNCEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "announcement.not-found"),
    USER_ANNOUNCEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "user.announcement.not-found"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
