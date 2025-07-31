package com.soomsoom.backend.common.exception

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class PersistenceErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    ENTITY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "persistence.entity.save-failed"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
