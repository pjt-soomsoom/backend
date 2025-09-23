package com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.entity

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
    name = "ad_reward_logs",
    indexes = [
        Index(name = "idx_arl_user_ad_created", columnList = "user_id, adUnitId, created_at")
    ]
)
class AdRewardLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val adUnitId: String,
    @Column(unique = true)
    val transactionId: String,
    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "amount"))
    val amount: PointsEmbeddable,
) : BaseTimeEntity()
