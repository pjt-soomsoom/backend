package com.soomsoom.backend.application.port.`in`.purchase.dto

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.domain.purchase.model.aggregate.PurchaseLog
import java.time.LocalDateTime

data class PurchaseLogDto(
    val purchaseId: Long,
    val purchasedItem: ItemDto,
    val price: Int,
    val purchasedAt: LocalDateTime,
) {
    companion object {
        fun from(log: PurchaseLog, itemDto: ItemDto): PurchaseLogDto {
            return PurchaseLogDto(
                purchaseId = log.id,
                purchasedItem = itemDto,
                price = log.price.value,
                purchasedAt = log.createdAt!!
            )
        }
    }
}
