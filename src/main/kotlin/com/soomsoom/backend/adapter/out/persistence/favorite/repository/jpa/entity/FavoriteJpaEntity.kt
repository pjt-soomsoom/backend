package com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_favorite_user_activity", columnNames = ["user_id", "activity_id"])
    ]
)
class FavoriteJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val activityId: Long,
) : BaseTimeEntity()
