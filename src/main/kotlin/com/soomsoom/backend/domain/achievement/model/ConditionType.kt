package com.soomsoom.backend.domain.achievement.model

enum class ConditionType {
    // 마음 일기 관련
    DIARY_COUNT, // 총 일기 작성 횟수
    DIARY_STREAK, // 연속 일기 작성일 수
    DIARY_MONTHLY_COUNT, // 월간 일기 작성 개수

    // 명상 관련
    MEDITATION_COUNT, // 총 명상 완료 횟수
    MEDITATION_STREAK, // 연속 명상 완료일 수
    MEDITATION_LATE_NIGHT_STREAK, // 심야(20시~02시) 명상 연속 완료일 수
    MEDITATION_MONTHLY_COUNT, // 월간 명상 완료 횟수
    MEDITATION_TOTAL_SECONDS, // 명상 총 시간

    // 호흡 관련
    BREATHING_COUNT, // 총 호흡 완료 횟수
    BREATHING_STREAK, // 연속 호흡 완료일 수
    BREATHING_MULTI_TYPE_COUNT, // 여러 종류(4종) 호흡 완료 횟수
    BREATHING_TOTAL_SECONDS, // 호흡 총 시간

    // 히든 업적 관련
    HIDDEN_DIARY_PERFECT_MONTH, // 한 달(30일) 모든 일기 기록 여부 (값: 1이면 달성)
    HIDDEN_EMOTION_OVERCOME, // 최악의 기분 -> 최고의 기분 변화 여부 (값: 1이면 달성)
    HIDDEN_STAY_HOME_SCREEN, // 홈 화면 N분 이상 유지 (값: 누적 시간(분))
    HIDDEN_CUSTOMIZE_CHARACTER, // 캐릭터 꾸미기 N회 진행 (값: 누적 횟수)
}
