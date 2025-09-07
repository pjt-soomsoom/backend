package com.soomsoom.backend.application.port.`in`.purchase.query

import org.springframework.data.domain.Pageable

data class FindPurchaseHistoryCriteria(
    val userId: Long,
    val pageable: Pageable,
)
