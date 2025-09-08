package com.soomsoom.backend.adapter.out.persistence.common.entity

import com.soomsoom.backend.domain.item.model.vo.Stock
import jakarta.persistence.Embeddable

@Embeddable
data class StockEmbeddable(
    val totalQuantity: Int?,
    val currentQuantity: Int,
) {
    companion object {
        fun from(domain: Stock): StockEmbeddable {
            return StockEmbeddable(
                totalQuantity = domain.totalQuantity,
                currentQuantity = domain.currentQuantity
            )
        }
    }
}
