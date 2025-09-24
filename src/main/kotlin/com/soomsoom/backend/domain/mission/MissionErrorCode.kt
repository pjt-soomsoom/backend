package com.soomsoom.backend.domain.mission

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.Locale

enum class MissionErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "mission.not-found"),
    ALREADY_REWARDED(HttpStatus.CONFLICT, "mission.already-rewarded"),
    NOT_COMPLETED(HttpStatus.BAD_REQUEST, "mission.not-completed"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
