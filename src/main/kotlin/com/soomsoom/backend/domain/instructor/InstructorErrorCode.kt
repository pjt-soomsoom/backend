package com.soomsoom.backend.domain.instructor

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.*

enum class InstructorErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    INSTRUCTOR_NAME_CANNOT_BE_BLANK(HttpStatus.BAD_REQUEST, "instructor.name-cannot-be-blank"),
    INSTRUCTOR_ID_CANNOT_BE_NULL(HttpStatus.BAD_REQUEST, "instructor.id-cannot-be-null"),
    INSTRUCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "instructor.not-found"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
