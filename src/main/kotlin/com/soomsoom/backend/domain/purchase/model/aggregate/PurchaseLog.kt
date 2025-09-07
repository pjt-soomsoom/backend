package com.soomsoom.backend.domain.purchase.model.aggregate

import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import java.time.LocalDateTime

class PurchaseLog(
    val id: Long = 0L,
    val userId: Long,
    val itemId: Long,
    val price: Points,
    val acquisitionType: AcquisitionType, // 구매 시점의 획득 경로 기록
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        fun record(userId: Long, itemId: Long, price: Points, acquisitionType: AcquisitionType): PurchaseLog {
            return PurchaseLog(
                userId = userId,
                itemId = itemId,
                price = price,
                acquisitionType = acquisitionType
            )
        }
    }
}
