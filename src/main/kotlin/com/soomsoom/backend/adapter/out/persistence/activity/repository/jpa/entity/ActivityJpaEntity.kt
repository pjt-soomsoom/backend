package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType.JOINED
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table

@Entity
@Table(
    name = "activities",
    indexes = [
        // 1. 타입과 카테고리로 검색하는 `search` 쿼리 최적화
        Index(name = "idx_activities_type_category", columnList = "activity_type, category"),
        // 2. 강사 ID로 활동을 찾는 쿼리 최적화 (author)
        Index(name = "idx_activities_author_id", columnList = "author_id"),
        // 3. 강사 ID로 활동을 찾는 쿼리 최적화 (narrator)
        Index(name = "idx_activities_narrator_id", columnList = "narrator_id"),
        // 4. 삭제 상태 필터링을 위한 인덱스
        Index(name = "idx_activities_deleted_at", columnList = "deleted_at")
    ]
)
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "activity_type")
abstract class ActivityJpaEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,
    var title: String,

    @Column(name = "author_id")
    var authorId: Long,

    @Column(name = "narrator_id")
    var narratorId: Long,

    @Column(name = "duration_in_seconds")
    var durationInSeconds: Int,

    @Column(name = "thumbnail_image_url")
    var thumbnailImageUrl: String?,

    @Column(name = "audio_url")
    var audioUrl: String?,

    @Column(name = "thumbnail_file_key")
    var thumbnailFileKey: String?,

    @Column(name = "audio_file_key")
    var audioFileKey: String?,

    @Column(name = "mini_thumbnail_image_url")
    var miniThumbnailImageUrl: String?,

    @Column(name = "mini_thumbnail_file_key")
    var miniThumbnailFileKey: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: ActivityCategory,

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "activity_descriptions", joinColumns = [JoinColumn(name = "activity_id")])
    @OrderColumn(name = "sequence")
    @Column(name = "description", columnDefinition = "TEXT")
    var descriptions: MutableList<String>,

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "activity_completion_effects", joinColumns = [JoinColumn(name = "activity_id")])
    @OrderColumn(name = "sequence")
    @Column(name = "effect_text", columnDefinition = "TEXT")
    var completionEffectTexts: MutableList<String>,
) : BaseTimeEntity() {
    open fun update(activity: Activity) {
        this.title = activity.title
        this.descriptions = activity.descriptions.toMutableList()
        this.category = activity.category
        this.authorId = activity.authorId
        this.narratorId = activity.narratorId
        this.durationInSeconds = activity.durationInSeconds
        this.thumbnailImageUrl = activity.thumbnailImageUrl
        this.thumbnailFileKey = activity.thumbnailFileKey
        this.audioUrl = activity.audioUrl
        this.audioFileKey = activity.audioFileKey
        this.miniThumbnailImageUrl = activity.miniThumbnailImageUrl
        this.miniThumbnailFileKey = activity.miniThumbnailFileKey
        this.completionEffectTexts = activity.completionEffectTexts.toMutableList()
        this.deletedAt = activity.deletedAt
    }
}
