package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.dto.OwnedItemDto
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import org.springframework.data.domain.Page

interface FindUserItemUseCase {
    fun findOwnedItems(criteria: FindOwnedItemsCriteria): Page<OwnedItemDto>
    fun findEquippedItems(userId: Long): EquippedItemsDto
}
