package com.soomsoom.backend.domain.mission.model.vo

/**
 * 미션 완료 시 지급될 보상 정보를 나타내는 값 객체
 * 보상 자체의 정보와, 보상 수령 시 발송될 알림 내용을 포함하여 하나의 의미 단위로 캡슐화
 *
 * @property points 지급될 포인트입니다.
 * @property itemId 지급될 아이템의 ID입니다. 아이템 보상이 없을 경우 null일 수 있습니다.
 * @property notification 보상 수령 시 사용자에게 보낼 알림 메시지입니다.
 */
data class MissionReward(
    val points: Int?,
    val itemId: Long?,
    val notification: NotificationContent,
) {
    // 객체 생성 시 데이터의 유효성을 검사합니다. (불변성 유지)
    init {
        // 포인트와 아이템 둘 다 없는 보상은 존재할 수 없다는 비즈니스 규칙을 강제합니다.
        require(points != null || itemId != null) { "보상은 포인트 또는 아이템 중 적어도 하나는 포함해야 합니다." }
        // 포인트는 0 이상이어야 한다는 비즈니스 규칙을 강제합니다.
        require(points == null || points >= 0) { "포인트는 0 이상이어야 합니다." }
    }
}
