package com.soomsoom.backend.application.port.`in`.purchase.usecase

import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseCartItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseResultDto

interface PurchaseUseCase {
    fun purchaseItems(command: PurchaseItemsCommand): PurchaseResultDto
    fun purchaseCartItems(command: PurchaseCartItemsCommand): PurchaseResultDto
}
