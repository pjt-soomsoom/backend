package com.soomsoom.backend.domain.mission.model.aggregate

import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.MissionReward
import com.soomsoom.backend.domain.mission.model.vo.NotificationContent
import java.time.LocalDateTime

/**
 * 미션의 모든 정보를 정의하는 애그리거트 루트
 * 미션은 사용자가 달성해야 할 과제나 이벤트
 *
 * @property id 미션의 고유 식별자입니다.
 * @property type 미션을 식별하는 고유한 타입입니다. (예: 하루 첫 호흡, 7일 연속 출석 등)
 * @property title 사용자에게 보여질 미션의 제목입니다.
 * @property description 미션에 대한 상세 설명입니다.
 * @property targetValue 미션 달성을 위해 필요한 목표 횟수 또는 값입니다.
 * @property completionNotification 미션 조건을 '달성'했을 때 사용자에게 보낼 알림 메시지입니다.
 * @property reward 미션 완료 시 지급될 보상 정보입니다. 보상 수령 시의 알림 메시지를 포함합니다.
 * @property repeatableType 미션의 반복 주기(매일, 매주, 한번만)를 정의합니다.
 * @property claimType 보상 수령 방식(자동, 수동)을 정의합니다.
 * @property deletedAt 미션이 삭제(비활성화)된 시간입니다. Soft Delete를 위해 사용되며, null이 아니면 삭제된 상태입니다.
 */
class Mission(
    val id: Long,
    var type: MissionType,
    var title: String,
    var description: String,
    var targetValue: Int,
    var completionNotification: NotificationContent,
    var reward: MissionReward,
    var repeatableType: RepeatableType,
    var claimType: ClaimType,
    val createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {
    /**
     * 미션이 삭제(비활성화)되었는지 확인
     * @return 삭제되었다면 true, 아니면 false.
     */
    fun isDeleted(): Boolean = deletedAt != null

    /**
     * 미션을 삭제(비활성화) 상태로 변경합니다.
     * 이미 삭제된 미션에 대해서도 멱등성을 보장합니다.
     */
    fun softDelete() {
        if (!isDeleted()) {
            this.deletedAt = LocalDateTime.now()
        }
    }

    fun update(
        type: MissionType,
        title: String,
        description: String,
        targetValue: Int,
        completionNotification: NotificationContent,
        reward: MissionReward,
        repeatableType: RepeatableType,
        claimType: ClaimType,
    ) {
        this.type = type
        this.title = title
        this.description = description
        this.targetValue = targetValue
        this.completionNotification = completionNotification
        this.reward = reward
        this.repeatableType = repeatableType
        this.claimType = claimType
    }
}
