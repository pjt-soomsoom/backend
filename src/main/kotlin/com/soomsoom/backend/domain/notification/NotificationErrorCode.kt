package com.soomsoom.backend.domain.notification

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.Locale

enum class NotificationErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    /**
     * 알림 발송 이력을 찾을 수 없을 때 발생하는 에러
     */
    HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "notification.history.not-found"),

    /**
     * 알림 템플릿을 찾을 수 없을 때 발생하는 에러
     */
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "notification.template.not-found"),

    /**
     * 사용자 디바이스 정보를 찾을 수 없을 때 발생하는 에러
     */
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "notification.device.not-found"),

    /**
     * 알림 메시지를 찾을 수 없을 때 발생하는 에러
     */
    MESSAGE_VARIATION_NOT_FOUND(HttpStatus.NOT_FOUND, "notification.message-variation.not-found"),
    ;
    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
