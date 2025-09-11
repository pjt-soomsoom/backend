package com.soomsoom.backend.application.port.`in`.banner.dto

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.banner.model.Banner

data class BannerAdminResult(
    val bannerId: Long,
    val imageUrl: String,
    val activityType: ActivityType,
    val description: String,
    val buttonText: String,
    val linkedActivityId: Long,
    val displayOrder: Int,
    val isActive: Boolean,
)

fun Banner.toAdminResult(activity: Activity): BannerAdminResult {
    return BannerAdminResult(
        bannerId = this.id,
        imageUrl = this.imageUrl,
        activityType = activity.type,
        description = this.description,
        buttonText = this.buttonText,
        linkedActivityId = this.linkedActivityId,
        displayOrder = this.displayOrder,
        isActive = this.isActive
    )
}
