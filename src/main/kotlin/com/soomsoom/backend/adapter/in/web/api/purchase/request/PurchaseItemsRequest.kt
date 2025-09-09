package com.soomsoom.backend.adapter.`in`.web.api.purchase.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PositiveOrZero

data class PurchaseItemsRequest(
    @field:NotEmpty(message = "구매할 아이템 ID 목록은 비어 있을 수 없습니다.")
    val itemIds: List<Long>,
    @field:PositiveOrZero(message = "예상 결제 금액은 0 이상이어야 합니다.")
    val expectedTotalPrice: Int,
)
