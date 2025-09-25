package com.soomsoom.backend.domain.mission.model.enums

/**
 * 미션의 종류를 정의하는 Enum 입니다.
 * 이는 미션의 비즈니스 로직을 구분
 */
enum class MissionType {
    CONSECUTIVE_ATTENDANCE, // 연속 출석
    ATTENDANCE_COUNT, // 출석 횟수
    DIARY_COUNT, // 마음 일기 작성 횟수
    DAILY_BREATHING_COUNT, // 일일 호흡 횟수
    FIRST_EVER_BREATHING, // 생애 첫 호흡
    FIRST_PAGE_VISIT, // 페이지 첫 방문
}
