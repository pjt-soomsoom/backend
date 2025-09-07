package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.domain.user.model.entity.OwnedItem
import org.springframework.data.domain.Page

interface OwnedItemPort {
    fun save(ownedItem: OwnedItem): OwnedItem
    fun saveAll(ownedItems: List<OwnedItem>): List<OwnedItem>
    fun findByUserId(criteria: FindOwnedItemsCriteria): Page<OwnedItem>
    fun countByUserIdAndItemIdIn(userId: Long, itemIds: List<Long>): Int
}
