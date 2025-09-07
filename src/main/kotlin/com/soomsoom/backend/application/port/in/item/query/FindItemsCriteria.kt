package com.soomsoom.backend.application.port.`in`.item.query

import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.ItemType
import org.springframework.data.domain.Pageable

data class FindItemsCriteria(
    val userId: Long,
    val itemType: ItemType?,
    val sortCriteria: ItemSortCriteria,
    val excludeOwned: Boolean,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val pageable: Pageable,
)

enum class ItemSortCriteria {
    RECOMMENDED,
    LATEST,
}
