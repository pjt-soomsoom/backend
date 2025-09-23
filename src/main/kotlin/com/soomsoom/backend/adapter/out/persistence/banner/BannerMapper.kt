package com.soomsoom.backend.adapter.out.persistence.banner

import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity.BannerJpaEntity
import com.soomsoom.backend.domain.banner.model.Banner

fun Banner.toEntity(): BannerJpaEntity {
    val entity = BannerJpaEntity(
        id = this.id,
        description = this.description,
        buttonText = this.buttonText,
        imageUrl = this.imageUrl,
        imageFileKey = this.imageFileKey,
        linkedActivityId = this.linkedActivityId,
        displayOrder = this.displayOrder,
        active = this.isActive
    )
    entity.deletedAt = this.deletedAt
    return entity
}

fun BannerJpaEntity.toDomain(): Banner {
    return Banner(
        id = this.id,
        description = this.description,
        buttonText = this.buttonText,
        imageUrl = this.imageUrl,
        imageFileKey = this.imageFileKey,
        linkedActivityId = this.linkedActivityId,
        displayOrder = this.displayOrder,
        isActive = this.active,
        deletedAt = this.deletedAt,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt
    )
}
