package com.soomsoom.backend.application.service.favorite.command

import com.soomsoom.backend.application.port.`in`.favorite.usecase.command.DeleteFavoriteUseCase
import com.soomsoom.backend.application.port.out.favorite.FavoritePort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteFavoriteService(
    private val favoritePort: FavoritePort,
) : DeleteFavoriteUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        favoritePort.deleteByUserId(userId)
    }
}
