package com.soomsoom.backend.application.port.`in`.banner.dto

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.banner.model.Banner

data class BannerResult(
    val bannerId: Long,
    val imageUrl: String,
    val activityType: ActivityType,
    val description: String,
    val buttonText: String,
    val linkedActivityId: Long,
    val displayOrder: Int,
)

fun Banner.toResult(activity: Activity): BannerResult {
    return BannerResult(
        bannerId = this.id,
        imageUrl = this.imageUrl,
        activityType = activity.type,
        description = this.description,
        buttonText = this.buttonText,
        linkedActivityId = this.linkedActivityId,
        displayOrder = this.displayOrder
    )
}
