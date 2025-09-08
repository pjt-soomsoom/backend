package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.CartJpaEntity
import com.soomsoom.backend.domain.user.model.aggregate.Cart
import com.soomsoom.backend.domain.user.model.entity.CartItem

fun Cart.toEntity(existingEntity: CartJpaEntity?): CartJpaEntity {
    val entity = existingEntity ?: CartJpaEntity(id = this.id, userId = this.userId)

    val currentItemIdsInDomain = this.items.map { it.itemId }.toSet()
    val currentItemIdsInEntity = entity.items.map { it.itemId }.toSet()

    entity.items.removeIf { it.itemId !in currentItemIdsInDomain }

    currentItemIdsInDomain.filterNot { it in currentItemIdsInEntity }.forEach { entity.addItem(it) }

    return entity
}

fun CartJpaEntity.toDomain(): Cart {
    return Cart(
        id = this.id,
        userId = this.userId,
        items = this.items.map {
            CartItem(
                id = it.id,
                cartId = this.id,
                itemId = it.itemId,
                addedAt = it.createdAt!!
            )
        }.toMutableList()
    )
}
