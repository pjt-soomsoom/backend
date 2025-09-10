package com.soomsoom.backend.application.port.`in`.favorite.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType

data class FavoriteActivityResult(
    val activityId: Long,
    val type: ActivityType,
    val title: String,
    val thumbnailImageUrl: String?,
    val durationInSeconds: Int,
) {
    companion object {
        fun from(activity: Activity): FavoriteActivityResult {
            val activityType = when (activity) {
                is BreathingActivity -> ActivityType.BREATHING
                is MeditationActivity -> ActivityType.MEDITATION
                else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
            }

            return FavoriteActivityResult(
                activityId = activity.id!!,
                type = activityType,
                title = activity.title,
                thumbnailImageUrl = activity.thumbnailImageUrl,
                durationInSeconds = activity.durationInSeconds
            )
        }
    }
}
