package com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "follows",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_follow_follower_followee", columnNames = ["follower_id", "followee_id"])
    ]
)
class FollowJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val followerId: Long,
    val followeeId: Long,
) : BaseTimeEntity()
