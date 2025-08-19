package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import jakarta.persistence.CascadeType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderColumn
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "breathing_activities")
@DiscriminatorValue("BREATHING")
@PrimaryKeyJoinColumn(name = "activity_id")
class BreathingActivityJpaEntity(
    title: String,
    descriptions: MutableList<String>,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    audioUrl: String?,

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
    thumbnailFileKey = null,
    audioFileKey = null
) {
    override fun update(activity: Activity) {
        super.update(activity)
        if (activity is BreathingActivity) {
            this.timeline.clear()
            this.timeline.addAll(activity.timeline.map { TimelineEventJpaEntity.from(it) })
        }
    }
}
