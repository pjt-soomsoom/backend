package com.soomsoom.backend.domain.purchase

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class PurchaseErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    PRICE_MISMATCH(HttpStatus.CONFLICT, "purchase.price-mismatch"),
    ALREADY_PURCHASE(HttpStatus.CONFLICT, "purchase.already-purchase"),

    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
