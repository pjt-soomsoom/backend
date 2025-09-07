package com.soomsoom.backend.domain.item.model.entity

class ItemSales(
    val itemId: Long,
    var salesCount: Long,
) {
    fun increase() {
        this.salesCount++
    }
}
