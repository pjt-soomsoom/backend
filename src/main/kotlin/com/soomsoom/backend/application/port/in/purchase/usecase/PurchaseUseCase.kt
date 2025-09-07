package com.soomsoom.backend.application.port.`in`.purchase.usecase

import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseCartItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseItemCommand
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseResultDto

interface PurchaseUseCase {
    fun purchaseItem(command: PurchaseItemCommand): PurchaseResultDto
    fun purchaseCartItems(command: PurchaseCartItemsCommand): PurchaseResultDto
}
