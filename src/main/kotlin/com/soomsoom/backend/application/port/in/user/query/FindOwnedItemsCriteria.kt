package com.soomsoom.backend.application.port.`in`.user.query

import com.soomsoom.backend.domain.item.model.enums.ItemType
import org.springframework.data.domain.Pageable

data class FindOwnedItemsCriteria(
    val userId: Long,
    val itemType: ItemType?,
    val pageable: Pageable,
)
