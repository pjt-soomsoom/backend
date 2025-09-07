package com.soomsoom.backend.common.exception

import com.soomsoom.backend.common.exception.DomainErrorReason.COLLECTION_ALREADY_OWNED
import com.soomsoom.backend.common.exception.DomainErrorReason.DELETED_ITEM
import com.soomsoom.backend.common.exception.DomainErrorReason.DUPLICATE_SLOT_IN_COLLECTION
import com.soomsoom.backend.common.exception.DomainErrorReason.EMPTY_ITEMS_IN_COLLECTION
import com.soomsoom.backend.common.exception.DomainErrorReason.ITEM_ALREADY_OWNED
import com.soomsoom.backend.common.exception.DomainErrorReason.ITEM_NOT_OWNED
import com.soomsoom.backend.common.exception.DomainErrorReason.ITEM_SOLD_OUT
import com.soomsoom.backend.common.exception.DomainErrorReason.NOT_ENOUGH_POINTS
import com.soomsoom.backend.common.exception.DomainErrorReason.NOT_PURCHASABLE_ITEM
import com.soomsoom.backend.domain.item.CollectionErrorCode
import com.soomsoom.backend.domain.item.ItemErrorCode
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

    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleDomainValidationException(e: RuntimeException): ResponseEntity<String> {
        val errorCode: ErrorCode = when (e.message) {
            // Item
            NOT_PURCHASABLE_ITEM -> ItemErrorCode.NOT_PURCHASABLE
            DELETED_ITEM -> ItemErrorCode.DELETED
            ITEM_SOLD_OUT -> ItemErrorCode.SOLD_OUT
            // Collection
            DUPLICATE_SLOT_IN_COLLECTION -> CollectionErrorCode.DUPLICATE_SLOT
            EMPTY_ITEMS_IN_COLLECTION -> CollectionErrorCode.EMPTY_ITEMS_IN_COLLECTION
            COLLECTION_ALREADY_OWNED -> CollectionErrorCode.ALREADY_OWNED
            // User-Item
            ITEM_ALREADY_OWNED -> UserErrorCode.ITEM_ALREADY_OWNED
            NOT_ENOUGH_POINTS -> UserErrorCode.NOT_ENOUGH_POINTS
            ITEM_NOT_OWNED -> UserErrorCode.ITEM_NOT_OWNED
            // 번역 규칙이 없는 경우, 예측하지 못한 서버 내부 오류로 처리
            else -> return handleException(e as Exception)
        }

        return ResponseEntity
            .status(errorCode.status)
            .body(errorCode.message(messageSource))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.")
    }
}
