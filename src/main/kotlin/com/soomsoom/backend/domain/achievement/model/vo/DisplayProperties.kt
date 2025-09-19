package com.soomsoom.backend.domain.achievement.model.vo

import org.springframework.context.MessageSource
import java.util.Locale

/**
 * ConditionType을 화면에 어떻게 표시할지에 대한 규칙을 담는 값 객체
 */
data class DisplayProperties(
    val descriptionKey: String,
    val progressUnitKey: String,
    val descriptionDivisor: Int = 1,
    val progressDivisor: Int = 1,
) {
    /**
     * 진행도 UI에 표시될 값으로 변환 (예: 1800(초) -> 30(분))
     */
    fun toProgressValue(value: Int): Int {
        return value / this.progressDivisor
    }

    /**
     * MessageSource를 이용해 완성된 설명 문자열을 반환
     */
    fun formatDescription(messageSource: MessageSource, value: Int): String {
        val descriptionTemplate = messageSource.getMessage(descriptionKey, null, Locale.KOREA)
        val unit = messageSource.getMessage(progressUnitKey, null, Locale.KOREA)

        // 설명에 필요한 값으로 변환 (예: 3600(초) -> 1(시간))
        val descriptionValue = value / this.descriptionDivisor

        return String.format(descriptionTemplate, descriptionValue, unit)
    }

    /**
     * MessageSource를 이용해 진행도 단위를 반환
     */
    fun getProgressUnit(messageSource: MessageSource): String {
        return messageSource.getMessage(progressUnitKey, null, Locale.KOREA)
    }
}
