package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.activity.model.Activity
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType.JOINED
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table

@Entity
@Table(name = "activities")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "acitivity_type")
abstract class ActivityJpaEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,
    var title: String,
    var authorId: Long,
    var narratorId: Long,
    var durationInSeconds: Int,
    var thumbnailImageUrl: String?,
    var audioUrl: String?,
    var thumbnailFileKey: String?,
    var audioFileKey: String?,

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "activity_descriptions", joinColumns = [JoinColumn(name = "activity_id")])
    @OrderColumn(name = "sequence")
    @Column(name = "description", columnDefinition = "TEXT")
    var descriptions: MutableList<String>,
) : BaseTimeEntity() {
    open fun update(activity: Activity) {
        this.title = activity.title
        this.descriptions = activity.descriptions.toMutableList()
        this.authorId = activity.authorId
        this.narratorId = activity.narratorId
        this.durationInSeconds = activity.durationInSeconds
        this.thumbnailImageUrl = activity.thumbnailImageUrl
        this.thumbnailFileKey = activity.thumbnailFileKey
        this.audioUrl = activity.audioUrl
        this.audioFileKey = activity.audioFileKey
        this.deletedAt = activity.deletedAt
    }
}
