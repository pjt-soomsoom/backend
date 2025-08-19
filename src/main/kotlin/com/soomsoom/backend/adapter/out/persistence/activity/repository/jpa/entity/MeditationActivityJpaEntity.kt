package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.Activity
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "meditation_activities")
@DiscriminatorValue("MEDITATION")
@PrimaryKeyJoinColumn(name = "activity_id")
class MeditationActivityJpaEntity(
    title: String,
    descriptions: MutableList<String>,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    audioUrl: String?,
) : ActivityJpaEntity(
    title = title,
    descriptions = descriptions,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    thumbnailImageUrl = thumbnailImageUrl,
    audioUrl = audioUrl,
    thumbnailFileKey = null,
    audioFileKey = null
) {
    override fun update(activity: Activity) {
        super.update(activity)
    }
}
