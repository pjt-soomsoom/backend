package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import jakarta.persistence.CascadeType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderColumn

@Entity
@DiscriminatorValue("BREATHING")
class BreathingActivityJpaEntity(
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

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "activity_id")
    @OrderColumn(name = "sequence")
    var timeline: MutableList<TimelineEventJpaEntity>,
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
        if (activity is BreathingActivity) {
            this.timeline.clear()
            this.timeline.addAll(activity.timeline.map { TimelineEventJpaEntity.from(it) })
        }
    }
}
