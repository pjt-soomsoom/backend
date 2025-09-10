package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto

interface FindEquippedItemsUseCase {
    fun findEquippedItems(userId: Long): EquippedItemsDto
}
