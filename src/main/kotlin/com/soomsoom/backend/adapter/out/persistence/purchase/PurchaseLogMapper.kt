package com.soomsoom.backend.adapter.out.persistence.purchase

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.PurchaseLogJpaEntity
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.purchase.model.aggregate.PurchaseLog

fun PurchaseLog.toEntity(): PurchaseLogJpaEntity {
    return PurchaseLogJpaEntity(
        id = this.id,
        userId = this.userId,
        itemId = this.itemId,
        price = PointsEmbeddable(this.price.value),
        acquisitionType = this.acquisitionType
    )
}

fun PurchaseLogJpaEntity.toDomain(): PurchaseLog {
    return PurchaseLog(
        id = this.id,
        userId = this.userId,
        itemId = this.itemId,
        price = Points(this.price.value),
        acquisitionType = this.acquisitionType,
        createdAt = this.createdAt
    )
}
