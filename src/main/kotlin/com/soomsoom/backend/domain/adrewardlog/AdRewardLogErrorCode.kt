package com.soomsoom.backend.domain.adrewardlog

import com.soomsoom.backend.common.exception.ErrorCode
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import java.util.Locale

enum class AdRewardLogErrorCode(
    private val httpStatus: HttpStatus,
    private val messageKey: String,
) : ErrorCode {
    // 이미 처리된 트랜잭션에 대한 보상 요청이 다시 들어온 경우 사용
    AD_REWARD_ALREADY_GRANTED(HttpStatus.CONFLICT, "ad-reward-log.already-granted"),
    ;

    override val status: HttpStatus
        get() = this.httpStatus

    override val errorName: String
        get() = this.name

    override fun message(messageSource: MessageSource): String {
        return messageSource.getMessage(messageKey, null, Locale.getDefault())
    }
}
