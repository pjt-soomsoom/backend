package com.soomsoom.backend.application.port.`in`.purchase.usecase

interface DeletePurchaseLogUseCase {
    fun deleteByUserId(userId: Long)
}
