package com.soomsoom.backend.adapter.`in`.web.api.user.request

import jakarta.validation.constraints.NotEmpty

data class AddItemsToCartRequest(
    @field:NotEmpty(message = "장바구니에 추가할 아이템 ID 목록은 비어 있을 수 없습니다.")
    val itemIds: List<Long>,
)
