package com.soomsoom.backend.application.port.`in`.favorite.usecase.command

import com.soomsoom.backend.application.port.`in`.favorite.command.ToggleFavoriteCommand
import com.soomsoom.backend.application.port.`in`.favorite.dto.ToggleFavoriteResult

interface ToggleFavoriteUseCase {
    fun toggle(command: ToggleFavoriteCommand): ToggleFavoriteResult
}
