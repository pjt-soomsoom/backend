package com.soomsoom.backend.application.port.out.item

import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.aggregate.Item
import org.springframework.data.domain.Page

interface ItemPort {

    fun save(item: Item): Item
    fun findById(itemId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Item?
    fun findAllByIds(itemIds: List<Long>, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): List<Item>
    fun search(criteria: FindItemsCriteria): Page<Item>
    fun delete(item: Item)
    fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<Item>
    fun findAllByIdsForUpdate(itemIds: List<Long>): List<Item>
    fun findByIdForUpdate(itemId: Long): Item?
}
