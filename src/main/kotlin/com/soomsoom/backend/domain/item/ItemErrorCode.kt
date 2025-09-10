package com.soomsoom.backend.domain.item

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class ItemErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "item.not-found"),
    SOLD_OUT(HttpStatus.CONFLICT, "item.sold-out"),
    NOT_PURCHASABLE(HttpStatus.BAD_REQUEST, "item.not-purchasable"),
    DELETED(HttpStatus.NOT_FOUND, "item.deleted"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
