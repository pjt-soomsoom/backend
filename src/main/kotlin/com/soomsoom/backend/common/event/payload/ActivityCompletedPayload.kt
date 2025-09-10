package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import java.time.LocalDateTime

/**
 * '활동 완료' 이벤트의 내용물
 * 업적 시스템에서 완료 횟수, 연속 달성 등의 업적을 확인
 */
data class ActivityCompletedPayload(
    val userId: Long,
    val activityId: Long,
    val activityType: ActivityType,
    val completedAt: LocalDateTime,
) : Payload
