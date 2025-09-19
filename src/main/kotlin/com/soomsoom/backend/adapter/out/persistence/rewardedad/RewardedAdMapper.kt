package com.soomsoom.backend.adapter.out.persistence.rewardedad

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.entity.RewardedAdJpaEntity
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.rewardedad.model.RewardedAd

fun RewardedAd.toEntity(): RewardedAdJpaEntity {
    return RewardedAdJpaEntity(
        id = this.id ?: 0,
        title = this.title,
        adUnitId = this.adUnitId,
        rewardAmount = PointsEmbeddable(this.rewardAmount.value),
        active = this.active
    )
}

fun RewardedAdJpaEntity.toDomain(): RewardedAd {
    return RewardedAd(
        id = this.id,
        title = this.title,
        adUnitId = this.adUnitId,
        rewardAmount = Points(this.rewardAmount.value),
        active = this.active
    )
}
