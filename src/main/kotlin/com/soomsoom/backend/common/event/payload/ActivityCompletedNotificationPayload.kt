package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload

/**
 * '활동 완료' 이벤트의 내용물
 * 업적 시스템에서 완료 횟수, 연속 달성 등의 업적을 확인하고, 사용자에게 알림을 보낼 수 있음
 */
data class ActivityCompletedNotificationPayload(
    val userId: Long,
    val activityId: Long,
) : NotificationPayload
