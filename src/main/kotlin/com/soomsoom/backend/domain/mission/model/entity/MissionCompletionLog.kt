package com.soomsoom.backend.domain.mission.model.entity

import java.time.LocalDateTime

/**
 * 사용자가 특정 미션을 언제 '달성'했고, 언제 '보상'받았는지를 기록하는 엔티티
 * 이 객체는 미션 완료 여부와 보상 지급 여부를 판단하는 핵심 근거 데이터
 *
 * @property id 로그의 고유 식별자입니다.
 * @property userId 미션을 달성한 사용자의 ID입니다.
 * @property missionId 달성한 미션의 ID입니다.
 * @property completedAt 미션 조건을 처음 만족시킨 시간입니다.
 * @property rewardedAt 사용자가 보상을 수령한 시간입니다. 아직 수령하지 않았다면 null 입니다.
 */
class MissionCompletionLog(
    val id: Long = 0,
    val userId: Long,
    val missionId: Long,
    val completedAt: LocalDateTime,
    var rewardedAt: LocalDateTime? = null,
) {
    /**
     * 이 미션에 대한 보상이 지급되었는지 여부를 반환
     * @return 보상을 받았으면 true, 아니면 false.
     */
    fun isRewarded(): Boolean = rewardedAt != null

    /**
     * 미션 보상을 지급 처리하고, 보상 수령 시간을 현재 시간으로 기록
     * 이미 보상이 지급된 경우, 상태를 변경하지 않고 idempotent하게 동작
     */
    fun markAsRewarded() {
        if (isRewarded()) {
            return
        }
        this.rewardedAt = LocalDateTime.now()
    }
}
