package com.soomsoom.backend.application.port.`in`.user.query

import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.ItemType
import org.springframework.data.domain.Pageable

data class FindOwnedItemsCriteria(
    val userId: Long,
    val itemType: ItemType?,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val pageable: Pageable,
)
