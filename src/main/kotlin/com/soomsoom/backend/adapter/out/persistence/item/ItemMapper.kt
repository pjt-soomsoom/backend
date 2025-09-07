package com.soomsoom.backend.adapter.out.persistence.item

import com.soomsoom.backend.adapter.out.persistence.item.repository.jpa.entity.ItemJpaEntity
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.vo.Stock

fun ItemJpaEntity.toDomain(): Item {
    return Item(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = Points(this.price),
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        stock = Stock(this.totalQuantity, this.currentQuantity),
        createdAt = this.createdAt,
        deletedAt = this.deletedAt
    )
}

fun Item.toJpaEntity(): ItemJpaEntity {
    return ItemJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = this.price.value,
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        totalQuantity = this.stock.totalQuantity,
        currentQuantity = this.stock.currentQuantity
    )
}
