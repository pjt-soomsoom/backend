package com.soomsoom.backend.application.port.`in`.activity.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.SoundEffectActivity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType

data class InstructorActivitySummaryResult(
    val activityId: Long,
    val type: ActivityType,
    val title: String,
    val thumbnailImageUrl: String?,
    val durationInSeconds: Int,
    val isFavorited: Boolean,
) {
    companion object {
        fun from(activity: Activity, isFavorited: Boolean): InstructorActivitySummaryResult {
            val activityType = when (activity) {
                is BreathingActivity -> ActivityType.BREATHING
                is MeditationActivity -> ActivityType.MEDITATION
                is SoundEffectActivity -> ActivityType.SOUND_EFFECT
                else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
            }

            return InstructorActivitySummaryResult(
                activityId = activity.id!!,
                type = activityType,
                title = activity.title,
                thumbnailImageUrl = activity.thumbnailImageUrl,
                durationInSeconds = activity.durationInSeconds,
                isFavorited = isFavorited
            )
        }
    }
}
