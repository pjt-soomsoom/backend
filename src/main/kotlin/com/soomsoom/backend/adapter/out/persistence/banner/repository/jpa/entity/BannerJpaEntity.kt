package com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "banners")
class BannerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false)
    var buttonText: String,

    @Column(nullable = false)
    var imageUrl: String,

    @Column(nullable = false)
    var imageFileKey: String,

    @Column(nullable = false)
    var linkedActivityId: Long,

    @Column(nullable = false)
    var displayOrder: Int,

    @Column(nullable = false)
    var isActive: Boolean,
) : BaseTimeEntity()
