package com.soomsoom.backend.common.exception

import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource,
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<String> {
        val first = e.bindingResult.allErrors.stream().findFirst().get()
        val message = first.defaultMessage
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(message)
    }

    @ExceptionHandler(SoomSoomException::class)
    fun handleSoomSoomException(e: SoomSoomException): ResponseEntity<String> {
        val errorCode = e.errorCode
        val status = errorCode.status
        val message = errorCode.message(messageSource)
        return ResponseEntity
            .status(status)
            .body(message)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<String> {
        return ResponseEntity
            .status(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH.status)
            .body(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH.message(messageSource))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<String> {
        return ResponseEntity
            .status(UserErrorCode.ACCESS_DENIED.status)
            .body(UserErrorCode.ACCESS_DENIED.message(messageSource))
    }
}
