package com.soomsoom.backend.adapter.out.persistence.adrewardlog

import com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.entity.AdRewardLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.domain.adrewardlog.model.AdRewardLog
import com.soomsoom.backend.domain.common.vo.Points

fun AdRewardLog.toEntity(): AdRewardLogJpaEntity {
    return AdRewardLogJpaEntity(
        id = this.id ?: 0,
        userId = this.userId,
        adUnitId = this.adUnitId,
        transactionId = this.transactionId,
        amount = PointsEmbeddable(this.amount.value)
    )
}

fun AdRewardLogJpaEntity.toDomain(): AdRewardLog {
    return AdRewardLog(
        id = this.id,
        userId = this.userId,
        adUnitId = this.adUnitId,
        transactionId = this.transactionId,
        amount = Points(this.amount.value),
        createdAt = this.createdAt
    )
}
