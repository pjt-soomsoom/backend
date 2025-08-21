package com.soomsoom.backend.adapter.out.persistence.favorite

import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.entity.FavoriteJpaEntity
import com.soomsoom.backend.domain.favoriote.model.Favorite

// JPA Entity -> Domain Model
fun FavoriteJpaEntity.toDomain(): Favorite {
    return Favorite(
        id = this.id,
        userId = this.userId,
        activityId = this.activityId,
        createdAt = this.createdAt
    )
}

// Domain Model -> JPA Entity
fun Favorite.toEntity(): FavoriteJpaEntity {
    return FavoriteJpaEntity(
        id = this.id ?: 0,
        userId = this.userId,
        activityId = this.activityId
    )
}
