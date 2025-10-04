package com.soomsoom.backend.application.port.`in`.activity.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.SoundEffectActivity
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.instructor.model.Instructor

data class ActivityResult(
    val id: Long,
    val type: ActivityType,
    val category: ActivityCategory,
    val title: String,
    val thumbnailImageUrl: String?,
    val descriptions: List<String>,
    val author: InstructorInfo,
    val narrator: InstructorInfo,
    val durationInSeconds: Int,
    val audioUrl: String?,
    val timeline: List<TimelineEvent>? = null,
    val completionEffectTexts: List<String>,
    val isFavorited: Boolean,
) {
    /**
     * 강사의 상세 정보를 담는 DTO
     */
    data class InstructorInfo(
        val id: Long,
        val name: String,
        val bio: String?,
        val profileImageUrl: String?,
    )

    companion object {
        fun from(activity: Activity, author: Instructor, narrator: Instructor, isFavorited: Boolean): ActivityResult {
            return when (activity) {
                is BreathingActivity -> ActivityResult(
                    id = activity.id!!,
                    type = activity.type,
                    title = activity.title,
                    thumbnailImageUrl = activity.thumbnailImageUrl,
                    descriptions = activity.descriptions,
                    author = InstructorInfo(author.id!!, author.name, author.bio, author.profileImageUrl),
                    narrator = InstructorInfo(narrator.id!!, narrator.name, narrator.bio, narrator.profileImageUrl),
                    durationInSeconds = activity.durationInSeconds,
                    audioUrl = activity.audioUrl,
                    timeline = activity.timeline,
                    category = activity.category,
                    completionEffectTexts = activity.completionEffectTexts,
                    isFavorited = isFavorited
                )
                is MeditationActivity -> ActivityResult(
                    id = activity.id!!,
                    type = activity.type,
                    title = activity.title,
                    thumbnailImageUrl = activity.thumbnailImageUrl,
                    descriptions = activity.descriptions,
                    author = InstructorInfo(author.id!!, author.name, author.bio, author.profileImageUrl),
                    narrator = InstructorInfo(narrator.id!!, narrator.name, narrator.bio, narrator.profileImageUrl),
                    durationInSeconds = activity.durationInSeconds,
                    audioUrl = activity.audioUrl,
                    isFavorited = isFavorited,
                    completionEffectTexts = activity.completionEffectTexts,
                    category = activity.category
                )
                is SoundEffectActivity -> ActivityResult(
                    id = activity.id!!,
                    type = activity.type,
                    title = activity.title,
                    thumbnailImageUrl = activity.thumbnailImageUrl,
                    descriptions = activity.descriptions,
                    author = InstructorInfo(author.id!!, author.name, author.bio, author.profileImageUrl),
                    narrator = InstructorInfo(narrator.id!!, narrator.name, narrator.bio, narrator.profileImageUrl),
                    durationInSeconds = activity.durationInSeconds,
                    audioUrl = activity.audioUrl,
                    isFavorited = isFavorited,
                    completionEffectTexts = activity.completionEffectTexts,
                    category = activity.category
                )
                else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
            }
        }
    }
}
