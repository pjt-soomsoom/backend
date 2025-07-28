package com.soomsoom.backend.common.exception

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val errorName: String
    fun message(messageSource: MessageSource): String
}
