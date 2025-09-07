package com.soomsoom.backend.application.port.out.item

import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Item
import org.springframework.data.domain.Page

interface ItemPort {
    fun save(item: Item): Item
    fun findById(itemId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Item?
    fun findItems(criteria: FindItemsCriteria): Page<Item>
    fun findItemsByIds(itemIds: List<Long>): List<Item>
    fun existsById(itemId: Long): Boolean
}
