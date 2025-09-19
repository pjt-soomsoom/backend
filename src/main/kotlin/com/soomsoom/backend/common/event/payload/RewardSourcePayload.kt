package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.reward.model.RewardSource
import com.soomsoom.backend.domain.reward.model.RewardType

data class RewardSourcePayload(
    val userId: Long,
    val rewardType: RewardType,
    val points: Int?,
    val itemId: Long?,
    val source: RewardSource,
    val sendNotification: Boolean,
    val notificationTitle: String?,
    val notificationBody: String?,
    val notificationImage: String?,
) : Payload
