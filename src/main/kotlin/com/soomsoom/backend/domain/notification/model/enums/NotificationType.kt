package com.soomsoom.backend.domain.notification.model.enums

enum class NotificationType {
    /** 마음 일기 작성을 독려하는 알림 */
    DIARY_REMINDER,

    /**
     * 미접속 사용자에게 보내는 재방문 유도 알림.
     * 미접속 1일차, 3일차, 7일차 등 구체적인 조건은 DB의 NotificationTemplate에서 관리하
     */
    RE_ENGAGEMENT,

    /** 새로운 업적을 달성했을 때 보내는 축하 알림 */
    ACHIEVEMENT_UNLOCKED,

    /** 관리자가 발송하는 새로운 소식 알림 */
    NEWS_UPDATE,

    /**
     * 특정 이벤트 달성으로 보상(포인트, 아이템 등)을 획득했을 때 보내는 알림.
     * (e.g., 7일 연속 출석 보상)
     */
    REWARD_ACQUIRED,
}
