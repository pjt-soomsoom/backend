package com.soomsoom.backend.application.port.`in`.item.query

import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Pageable

data class FindCollectionsCriteria(
    val userId: Long,
    val sortCriteria: CollectionSortCriteria,
    val excludeOwned: Boolean,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val pageable: Pageable,
)

enum class CollectionSortCriteria {
    POPULARITY, // 인기순 (판매순)
    PRICE_ASC,
    PRICE_DESC,
    CREATED,
}
