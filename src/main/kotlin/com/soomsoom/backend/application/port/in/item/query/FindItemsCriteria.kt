package com.soomsoom.backend.application.port.`in`.item.query

import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import org.springframework.data.domain.Pageable

data class FindItemsCriteria(
    val userId: Long,
    val itemType: ItemType?,
    val equipSlot: EquipSlot?,
    val sortCriteria: ItemSortCriteria,
    val excludeOwned: Boolean,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val acquisitionType: AcquisitionType?,
    val pageable: Pageable,
)

enum class ItemSortCriteria {
    POPULARITY, // 인기순 (판매순)
    PRICE_ASC, // 가격순
    PRICE_DESC, // 최신순
    CREATED,
}
