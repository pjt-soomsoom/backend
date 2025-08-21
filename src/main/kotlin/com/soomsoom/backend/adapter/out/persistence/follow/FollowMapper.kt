package com.soomsoom.backend.adapter.out.persistence.follow

import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.entity.FollowJpaEntity
import com.soomsoom.backend.domain.follow.model.Follow

fun FollowJpaEntity.toDomain(): Follow {
    return Follow(
        id = this.id,
        followerId = this.followerId,
        followeeId = this.followeeId,
        createdAt = this.createdAt
    )
}

// Domain Model -> JPA Entity
fun Follow.toEntity(): FollowJpaEntity {
    return FollowJpaEntity(
        id = this.id ?: 0,
        followerId = this.followerId,
        followeeId = this.followeeId
    )
}
