package com.soomsoom.backend.common.exception

import org.springframework.context.MessageSource

data class ErrorResponse (
    val code: String,
    val message: String,
) {
    companion object {
        fun of(errorCode: ErrorCode, messageSource: MessageSource): ErrorResponse {
            return ErrorResponse(
                code = errorCode.errorName,
                message = errorCode.message(messageSource)
            )
        }
    }
}
