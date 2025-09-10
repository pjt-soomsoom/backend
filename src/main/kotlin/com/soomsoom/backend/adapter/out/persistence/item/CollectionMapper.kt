package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.CollectionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import com.soomsoom.backend.domain.item.model.aggregate.Collection

fun Collection.toEntity(itemEntities: Set<ItemJpaEntity>): CollectionJpaEntity {
    return CollectionJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        imageFileKey = this.imageFileKey,
        lottieFileKey = this.lottieFileKey
    ).apply {
        this.items.addAll(itemEntities)
        this.deletedAt = this@toEntity.deletedAt
    }
}

fun CollectionJpaEntity.toDomain(): Collection {
    return Collection(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        imageFileKey = this.imageFileKey,
        lottieFileKey = this.lottieFileKey,
        items = this.items.map { it.toDomain() },
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
        deletedAt = this.deletedAt
    )
}
