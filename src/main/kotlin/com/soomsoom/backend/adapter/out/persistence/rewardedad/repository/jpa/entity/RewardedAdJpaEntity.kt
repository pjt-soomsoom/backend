package com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "rewarded_ads",
    indexes = [
        Index(name = "idx_rewarded_ads_active", columnList = "active")
    ]
)
class RewardedAdJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    @Column(unique = true)
    val adUnitId: String,
    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "reward_amount"))
    var rewardAmount: PointsEmbeddable,
    var active: Boolean,
) : BaseTimeEntity()
