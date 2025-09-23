package com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "banners",
    indexes = [
        Index(name = "idx_banners_active_deleted_order", columnList = "active, deleted_at, display_order")
    ]
)
class BannerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, name = "button_text")
    var buttonText: String,

    @Column(nullable = false, name = "image_url")
    var imageUrl: String,

    @Column(nullable = false, name = "image_file_key")
    var imageFileKey: String,

    @Column(nullable = false, name = "linked_activity_id")
    var linkedActivityId: Long,

    @Column(nullable = false, name = "display_order")
    var displayOrder: Int,

    @Column(nullable = false)
    var active: Boolean,
) : BaseTimeEntity()
