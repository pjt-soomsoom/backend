package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "meditation_activities")
@DiscriminatorValue("MEDITATION")
class MeditationActivityJpaEntity(
    title: String,
    descriptions: MutableList<String>,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    thumbnailFileKey: String?,
    audioUrl: String?,
    audioFileKey: String?,
    category: ActivityCategory,
    miniThumbnailImageUrl: String?,
    miniThumbnailFileKey: String?,
    completionEffectTexts: MutableList<String>,
) : ActivityJpaEntity(
    title = title,
    descriptions = descriptions,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    thumbnailImageUrl = thumbnailImageUrl,
    audioUrl = audioUrl,
    thumbnailFileKey = thumbnailFileKey,
    audioFileKey = audioFileKey,
    category = category,
    miniThumbnailImageUrl = miniThumbnailImageUrl,
    miniThumbnailFileKey = miniThumbnailFileKey,
    completionEffectTexts = completionEffectTexts
) {
    override fun update(activity: Activity) {
        super.update(activity)
    }
}
