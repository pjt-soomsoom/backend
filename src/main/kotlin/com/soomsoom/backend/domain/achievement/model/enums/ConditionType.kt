package com.soomsoom.backend.domain.achievement.model.enums

import com.soomsoom.backend.domain.achievement.model.vo.DisplayProperties

enum class ConditionType {
    // 마음 일기 관련
    DIARY_COUNT, // 총 일기 작성 횟수
    DIARY_STREAK, // 연속 일기 작성일 수
    DIARY_MONTHLY_COUNT, // 월간 일기 작성 개수

    // 명상 관련
    MEDITATION_COUNT, // 총 명상 완료 횟수
    MEDITATION_STREAK, // 연속 명상 완료일 수
    MEDITATION_LATE_NIGHT_COUNT, // 심야(20시~02시) 명상 연속 완료일 수
    MEDITATION_MONTHLY_COUNT, // 월간 명상 완료 횟수
    MEDITATION_TOTAL_MINUTES, // 명상 총 시간(분)

    // 호흡 관련
    BREATHING_COUNT, // 총 호흡 완료 횟수
    BREATHING_STREAK, // 연속 호흡 완료일 수
    BREATHING_MULTI_TYPE_COUNT, // 여러 종류(4종) 호흡 완료 횟수
    BREATHING_TOTAL_MINUTES, // 호흡 총 시간(분)
    BREATHING_MONTHLY_COUNT, // 월간 호흡 완료 횟수

    // 효과음 관련
    SOUND_EFFECT_TOTAL_MINUTES,

    // 히든 업적 관련
    HIDDEN_EMOTION_OVERCOME, // 최악의 기분 -> 최고의 기분 변화 여부 (값: 1이면 달성)
    HIDDEN_STAY_HOME_SCREEN, // 홈 화면 N분 이상 유지 (값: 누적 시간(분))
    HIDDEN_CUSTOMIZE_CHARACTER, // 캐릭터 꾸미기 N회 진행 (값: 누적 횟수)
    ;

    companion object {
        private const val ACHIEVEMENT_UNIT_COUNT = "achievement.unit.count"
        private const val ACHIEVEMENT_UNIT_DAY = "achievement.unit.day"
        private const val ACHIEVEMENT_UNIT_KIND = "achievement.unit.kind"
        private const val ACHIEVEMENT_UNIT_MINUTE = "achievement.unit.minute"
        private const val ACHIEVEMENT_UNIT_HOUR = "achievement.unit.hour"
        private const val ACHIEVEMENT_UNIT_SECONDS = "achievement.unit.second="

        private const val HOUR_DIVISOR = 3600
        private const val MINUTE_DIVISOR = 60

        private val displayPropertiesMap = mapOf(
            DIARY_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.DIARY_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            DIARY_STREAK to DisplayProperties(
                descriptionKey = "achievement.condition.DIARY_STREAK",
                progressUnitKey = ACHIEVEMENT_UNIT_DAY
            ),
            DIARY_MONTHLY_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.DIARY_MONTHLY_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            BREATHING_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.BREATHING_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            BREATHING_STREAK to DisplayProperties(
                descriptionKey = "achievement.condition.BREATHING_STREAK",
                progressUnitKey = ACHIEVEMENT_UNIT_DAY
            ),
            BREATHING_MONTHLY_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.BREATHING_MONTHLY_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            BREATHING_MULTI_TYPE_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.BREATHING_MULTI_TYPE_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_KIND
            ),
            BREATHING_TOTAL_MINUTES to DisplayProperties(
                descriptionKey = "achievement.condition.BREATHING_TOTAL_MINUTES",
                progressUnitKey = ACHIEVEMENT_UNIT_MINUTE,
                descriptionDivisor = MINUTE_DIVISOR,
                progressDivisor = MINUTE_DIVISOR
            ),
            SOUND_EFFECT_TOTAL_MINUTES to DisplayProperties(
                descriptionKey = "achievement.condition.SOUND_EFFECT_TOTAL_MINUTES",
                progressUnitKey = ACHIEVEMENT_UNIT_MINUTE,
                descriptionDivisor = MINUTE_DIVISOR,
                progressDivisor = MINUTE_DIVISOR
            ),
            MEDITATION_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.MEDITATION_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            MEDITATION_STREAK to DisplayProperties(
                descriptionKey = "achievement.condition.MEDITATION_STREAK",
                progressUnitKey = ACHIEVEMENT_UNIT_DAY
            ),
            MEDITATION_LATE_NIGHT_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.MEDITATION_LATE_NIGHT_STREAK",
                progressUnitKey = ACHIEVEMENT_UNIT_DAY
            ),
            MEDITATION_MONTHLY_COUNT to DisplayProperties(
                descriptionKey = "achievement.condition.MEDITATION_MONTHLY_COUNT",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            MEDITATION_TOTAL_MINUTES to DisplayProperties(
                descriptionKey = "achievement.condition.MEDITATION_TOTAL_MINUTES",
                progressUnitKey = ACHIEVEMENT_UNIT_MINUTE,
                descriptionDivisor = MINUTE_DIVISOR,
                progressDivisor = MINUTE_DIVISOR
            ),
            HIDDEN_CUSTOMIZE_CHARACTER to DisplayProperties(
                descriptionKey = "achievement.condition.HIDDEN_CUSTOMIZE_CHARACTER",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            ),
            HIDDEN_STAY_HOME_SCREEN to DisplayProperties(
                descriptionKey = "achievement.condition.HIDDEN_STAY_HOME_SCREEN",
                progressUnitKey = ACHIEVEMENT_UNIT_HOUR,
                descriptionDivisor = HOUR_DIVISOR,
                progressDivisor = MINUTE_DIVISOR
            ),
            HIDDEN_EMOTION_OVERCOME to DisplayProperties(
                descriptionKey = "achievement.condition.HIDDEN_EMOTION_OVERCOME",
                progressUnitKey = ACHIEVEMENT_UNIT_COUNT
            )

        )

        fun toDisplayProperties(type: ConditionType): DisplayProperties {
            return displayPropertiesMap[type]
                ?: DisplayProperties("achievement.condition.default", ACHIEVEMENT_UNIT_COUNT)
        }
    }
}
