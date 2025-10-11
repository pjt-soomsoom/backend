package com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.entity.FavoriteJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteJpaRepository : JpaRepository<FavoriteJpaEntity, Long> {
    fun findByUserIdAndActivityId(userId: Long, activityId: Long): FavoriteJpaEntity?
    fun deleteAllByUserId(userId: Long)
}
