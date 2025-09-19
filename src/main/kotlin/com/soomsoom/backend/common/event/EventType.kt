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

    // 아이템 구매(획득)
    ITEM_PURCHASED,

    // 아이템 착용
    ITEMS_EQUIPPED,

    // 오늘의 첫 접속
    FIRST_CONNECTION_OF_THE_DAY,

    // 사용자 인증 성공 이벤트
    USER_AUTHENTICATED,

    // 홈 화면 누적 시간
    SCREEN_TIME_ACCUMULATED,

    // 스케줄러가 주기적으로 실행될 때 발생하는 이벤트
    SCHEDULER_TICK,

    // 유저 생성
    USER_CREATED,

    // 공지
    ANNOUNCEMENT_CREATED,
    ANNOUNCEMENT_DELETED,

    // 보상 지급 요청 이벤트
    REWARD_SOURCE_TRIGGERED,

    // 보상 지급 완료 이벤트
    REWARD_COMPLETED,
}
