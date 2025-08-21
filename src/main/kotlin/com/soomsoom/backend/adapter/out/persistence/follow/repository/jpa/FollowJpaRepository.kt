package com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.entity.FollowJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FollowJpaRepository : JpaRepository<FollowJpaEntity, Long> {
    fun findByFollowerIdAndFolloweeId(followerId: Long, followeeId: Long): FollowJpaEntity?
}
