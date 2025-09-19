package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload

class RewardCompletedNotificationPayload(
    val userId: Long,
    val title: String,
    val body: String,
    val imageUrl: String?,
) : NotificationPayload
