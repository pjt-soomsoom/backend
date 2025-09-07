package com.soomsoom.backend.application.port.out.item

import com.soomsoom.backend.domain.item.model.entity.ItemSales

interface ItemSalesPort {
    fun findByItemId(itemId: Long): ItemSales?
    fun save(itemSales: ItemSales): ItemSales
}
