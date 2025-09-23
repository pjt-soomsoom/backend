package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload
import com.soomsoom.backend.domain.reward.model.RewardType

class RewardCompletedNotificationPayload(
    val userId: Long,
    val rewardType: RewardType,
    val title: String,
    val body: String,
    val points: Int?,
    val imageUrl: String?,
) : NotificationPayload
