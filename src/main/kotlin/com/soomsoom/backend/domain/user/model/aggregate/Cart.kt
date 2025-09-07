package com.soomsoom.backend.domain.user.model.aggregate

import com.soomsoom.backend.domain.user.model.entity.CartItem

class Cart(
    val id: Long = 0L,
    val userId: Long,
    items: MutableList<CartItem> = mutableListOf(),
) {
    private val _items: MutableList<CartItem> = items
    val items: List<CartItem>
        get() = _items.toList()

    fun addItem(itemId: Long) {
        if (_items.none { it.itemId == itemId }) {
            _items.add(
                CartItem(
                    cartId = this.id,
                    itemId = itemId
                )
            )
        }
    }

    fun removeItem(itemId: Long) {
        _items.removeIf { it.itemId == itemId }
    }

    fun clear() {
        _items.clear()
    }
}
