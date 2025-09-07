package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.PurchaseCartItemsCommand
import com.soomsoom.backend.application.port.`in`.user.dto.PurchaseResultDto

interface PurchaseCartItemsUseCase {
    fun purchaseCartItems(command: PurchaseCartItemsCommand): PurchaseResultDto
}
