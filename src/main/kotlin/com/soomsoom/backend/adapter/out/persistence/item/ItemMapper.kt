package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.common.entity.PointsEmbeddable
import com.soomsoom.backend.adapter.out.persistence.common.entity.StockEmbeddable
import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.vo.Stock

fun Item.toEntity(): ItemJpaEntity {
    return ItemJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = PointsEmbeddable(this.price.value),
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        imageFileKey = this.imageFileKey,
        lottieFileKey = this.lottieFileKey,
        stock = StockEmbeddable.from(this.stock)
    ).apply {
        this.deletedAt = this@toEntity.deletedAt
    }
}

fun ItemJpaEntity.toDomain(): Item {
    return Item(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = Points(this.price.value),
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        imageFileKey = this.imageFileKey,
        lottieFileKey = this.lottieFileKey,
        stock = Stock(this.stock.totalQuantity, this.stock.currentQuantity),
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
        deletedAt = this.deletedAt
    )
}
