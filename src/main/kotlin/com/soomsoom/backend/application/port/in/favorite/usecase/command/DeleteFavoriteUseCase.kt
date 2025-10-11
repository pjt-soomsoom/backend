package com.soomsoom.backend.application.port.`in`.favorite.usecase.command

interface DeleteFavoriteUseCase {
    fun deleteByUserId(userId: Long)
}
