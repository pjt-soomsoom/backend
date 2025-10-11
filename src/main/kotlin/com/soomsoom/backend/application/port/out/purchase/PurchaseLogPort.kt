package com.soomsoom.backend.application.port.out.purchase

import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import com.soomsoom.backend.domain.purchase.model.aggregate.PurchaseLog
import org.springframework.data.domain.Page

interface PurchaseLogPort {
    fun save(purchaseLog: PurchaseLog): PurchaseLog
    fun search(criteria: FindPurchaseHistoryCriteria): Page<PurchaseLog>
    fun deleteByUserId(userId: Long)
}
