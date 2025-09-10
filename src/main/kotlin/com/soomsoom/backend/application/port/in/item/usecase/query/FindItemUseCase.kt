package com.soomsoom.backend.application.port.`in`.item.usecase.query

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.query.FindItemsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page

interface FindItemUseCase {
    fun findItems(criteria: FindItemsCriteria): Page<ItemDto>
    fun findItem(userId: Long, itemId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): ItemDto
}
