package com.soomsoom.backend.application.helper

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class AchievementDisplayHelper(
    private val messageSource: MessageSource,
) {
    /**
     * 업적 조건에 대한 완전한 설명 텍스트를 생성
     * 예: "3일 연속 일기 작성"
     */
    fun formatDescription(type: ConditionType, targetValue: Int): String {
        val props = ConditionType.toDisplayProperties(type)
        val template = messageSource.getMessage(props.descriptionKey, null, Locale.KOREA)
        val unit = messageSource.getMessage(props.progressUnitKey, null, Locale.KOREA)

        // 설명에 필요한 값으로 변환 (예: 3600(초) -> 1(시간))
        val descriptionValue = targetValue / props.descriptionDivisor

        // %s가 있는 템플릿의 경우 단위를 포함하여 포맷팅
        return if (template.contains("%s")) {
            String.format(template, descriptionValue, unit)
        } else {
            template
        }
    }

    /**
     * 진행도 UI에 필요한 정보를 담은 ProgressInfo 객체를 생성
     */
    fun createProgressInfo(type: ConditionType, targetValue: Int, currentValue: Int?): ProgressInfo {
        val props = ConditionType.toDisplayProperties(type)

        // 진행도 UI에 표시될 값으로 변환 (예: 1800초 -> 30분)
        val progressTarget = targetValue / props.progressDivisor
        val progressCurrent = currentValue?.let { it / props.progressDivisor } ?: 0

        return ProgressInfo(
            currentValue = progressCurrent,
            targetValue = progressTarget,
            unit = messageSource.getMessage(props.progressUnitKey, null, Locale.KOREA)
        )
    }
}
