package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.CollectionJpaEntity
import com.soomsoom.backend.domain.item.model.aggregate.Collection
import com.soomsoom.backend.domain.item.model.vo.Points

fun CollectionJpaEntity.toDomain(): Collection {
    return Collection.from(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        basePrice = Points(this.basePrice),
        itemIds = this.itemIds,
        createdAt = this.createdAt,
        deletedAt = this.deletedAt
    )
}

fun Collection.toJpaEntity(): CollectionJpaEntity {
    return CollectionJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        basePrice = this.basePrice.value,
        itemIds = this.itemIds
    )
}
