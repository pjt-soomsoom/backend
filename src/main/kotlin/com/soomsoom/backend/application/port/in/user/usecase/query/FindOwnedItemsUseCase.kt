package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import org.springframework.data.domain.Page

interface FindOwnedItemsUseCase {
    fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<ItemDto>
}
