package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.PurchaseItemCommand
import com.soomsoom.backend.application.port.`in`.user.dto.OwnedItemDto

interface PurchaseItemUseCase {
    fun purchaseItem(command: PurchaseItemCommand): OwnedItemDto
}
