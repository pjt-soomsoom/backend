package com.soomsoom.backend.application.port.`in`.purchase.usecase

import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseLogDto
import com.soomsoom.backend.application.port.`in`.purchase.query.FindPurchaseHistoryCriteria
import org.springframework.data.domain.Page

interface FindPurchaseHistoryUseCase {
    fun findPurchaseHistory(criteria: FindPurchaseHistoryCriteria): Page<PurchaseLogDto>
}
