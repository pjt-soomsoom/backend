package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.domain.activity.model.TimelineEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "timeline_events",
    indexes = [
        Index(name = "idx_timeline_events_activity_id", columnList = "activity_id")
    ]
)
class TimelineEventJpaEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,
    var time: Double,
    var action: String,
    @Column(name = "text_content")
    var text: String,
    var duration: Double?,
) {
    companion object {
        fun from(domain: TimelineEvent): TimelineEventJpaEntity {
            return TimelineEventJpaEntity(
                id = domain.id ?: 0L,
                time = domain.time,
                action = domain.action,
                text = domain.text,
                duration = domain.duration
            )
        }
    }
}
