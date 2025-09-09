package com.soomsoom.backend.adapter.`in`.web.api.purchase.request

import jakarta.validation.constraints.PositiveOrZero

data class PurchaseCartItemsRequest(
    @field:PositiveOrZero(message = "예상 결제 금액은 0 이상이어야 합니다.")
    val expectedTotalPrice: Int,
)
