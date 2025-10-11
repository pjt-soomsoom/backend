package com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command

interface DeleteAdRewardLogUseCase {
    fun deleteByUserId(userId: Long)
}
