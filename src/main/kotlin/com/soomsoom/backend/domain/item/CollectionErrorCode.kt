package com.soomsoom.backend.domain.item

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.Locale

enum class CollectionErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "collection.not-found"),
    DUPLICATE_SLOT(HttpStatus.BAD_REQUEST, "collection.duplicate-slot"),
    EMPTY_ITEMS_IN_COLLECTION(HttpStatus.BAD_REQUEST, "collection.empty-items"),
    ALREADY_OWNED(HttpStatus.BAD_REQUEST, "collection.already-owned"),
    ;

    override val status: HttpStatus get() = this.httpStatus
    override val errorName: String get() = this.name
    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
