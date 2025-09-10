package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "sound_effect_activities")
@DiscriminatorValue("SOUND_EFFECT")
@PrimaryKeyJoinColumn(name = "activity_id")
class SoundEffectActivityJpaEntity(
    title: String,
    descriptions: MutableList<String>,
    category: ActivityCategory,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    thumbnailFileKey: String?,
    audioUrl: String?,
    audioFileKey: String?,
) : ActivityJpaEntity(
    title = title,
    descriptions = descriptions,
    category = category,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    thumbnailImageUrl = thumbnailImageUrl,
    audioUrl = audioUrl,
    thumbnailFileKey = thumbnailFileKey,
    audioFileKey = audioFileKey
) {
    override fun update(activity: Activity) {
        super.update(activity)
    }
}
