package com.soomsoom.backend.domain.diary

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class DiaryErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "diary.not-found"),
    ID_CANNOT_BE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "diary.id-cannot-be-null"),
    CREATED_AT_CANNOT_BE_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "diary.created-at-cannot-be-null"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "diary.forbidden"),
    DIARY_ALREADY_EXISTS(HttpStatus.CONFLICT, "diary.already-exists"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
