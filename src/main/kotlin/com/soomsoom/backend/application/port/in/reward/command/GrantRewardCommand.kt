package com.soomsoom.backend.application.port.`in`.reward.command

import com.soomsoom.backend.domain.reward.model.RewardSource
import com.soomsoom.backend.domain.reward.model.RewardType

data class GrantRewardCommand(
    val userId: Long,
    val rewardType: RewardType,
    val points: Int?,
    val itemId: Long?,
    val source: RewardSource,
    val sendNotification: Boolean,
    val notificationTitle: String?,
    val notificationBody: String?,
    val notificationImage: String?,
) {
    init {
        if (sendNotification) {
            require(!notificationTitle.isNullOrBlank()) { "Notification title is required." }
            require(!notificationBody.isNullOrBlank()) { "Notification body is required." }
        }
    }
}
