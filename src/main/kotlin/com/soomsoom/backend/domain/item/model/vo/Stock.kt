package com.soomsoom.backend.domain.item.model.vo

import com.soomsoom.backend.common.exception.DomainErrorReason

data class Stock(
    val totalQuantity: Int?, // 무한 재고의 경우 null
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
        if (totalQuantity == null) {
            return this
        }
        return this.copy(currentQuantity = this.currentQuantity - 1)
    }

    /**
     * 업데이트 시 재고를 조정하는 로직
     */
    fun adjust(newTotalQuantity: Int?): Stock {
        // 경우 1: 무한 재고 -> 무한 재고 (변경 없음)
        if (this.totalQuantity == null && newTotalQuantity == null) return this

        // 경우 2: 한정 재고 -> 무한 재고
        if (newTotalQuantity == null) {
            // totalQuantity는 null로, currentQuantity는 '판매 가능'을 의미하는 1로 설정
            return this.copy(totalQuantity = null, currentQuantity = 1)
        }

        // 경우 3: 무한 재고 -> 한정 재고 또는 한정 재고 -> 한정 재고
        val oldTotal = this.totalQuantity ?: 0
        val diff = newTotalQuantity - oldTotal
        val newCurrent = (this.currentQuantity + diff).coerceAtLeast(0)

        return Stock(
            totalQuantity = newTotalQuantity,
            currentQuantity = newCurrent
        )
    }
}
