package com.soomsoom.backend.common.event

enum class EventType {
    // 활동 완료 이벤트
    ACTIVITY_COMPLETED,

    // 사용자 누적 시간 갱신 이벤트
    USER_PLAY_TIME_ACCUMULATED,

    // 업적 달성 이벤트
    ACHIEVEMENT_ACHIEVED,

    // diary 작성 완료
    DIARY_CREATED,
}
