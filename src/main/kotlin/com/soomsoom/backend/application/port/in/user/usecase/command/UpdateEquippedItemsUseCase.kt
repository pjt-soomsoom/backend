package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.UpdateEquippedItemsCommand
import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto

interface UpdateEquippedItemsUseCase {
    fun updateEquippedItems(command: UpdateEquippedItemsCommand): EquippedItemsDto
}
