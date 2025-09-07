package com.soomsoom.backend.domain.item.model.vo

import com.soomsoom.backend.common.DomainErrorReason

data class Stock(
    val totalQuantity: Int?,
    val currentQuantity: Int,
) {
    init {
        totalQuantity?.let { require(it >= 0) { "총 재고량은 음수일 수 없습니다." } }
        require(currentQuantity >= 0) { "현재 재고는 음수일 수 없습니다." }
        totalQuantity?.let { require(currentQuantity <= it) { "현재 재고가 총 재고량보다 많을 수 없습니다." } }
    }

    fun isSoldOut(): Boolean = totalQuantity?.let { currentQuantity <= 0 } ?: false

    fun decrease(): Stock {
        check(!isSoldOut()) { DomainErrorReason.ITEM_SOLD_OUT }
        return this.copy(currentQuantity = this.currentQuantity - 1)
    }

    /**
     * 업데이트 시 재고를 조정하는 로직
     */
    fun adjust(newTotalQuantity: Int?): Stock {
        val oldTotal = this.totalQuantity ?: 0
        val newTotal = newTotalQuantity ?: 0

        val diff = newTotal - oldTotal
        val newCurrent = this.currentQuantity + diff

        return Stock(
            totalQuantity = newTotalQuantity,
            currentQuantity = newCurrent
        )
    }
}
