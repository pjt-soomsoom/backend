package com.soomsoom.backend.application.service.favorite.command

import com.soomsoom.backend.application.port.`in`.favorite.command.ToggleFavoriteCommand
import com.soomsoom.backend.application.port.`in`.favorite.dto.ToggleFavoriteResult
import com.soomsoom.backend.application.port.`in`.favorite.usecase.command.ToggleFavoriteUseCase
import com.soomsoom.backend.application.port.out.favorite.FavoritePort
import com.soomsoom.backend.domain.favoriote.model.Favorite
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class ToggleFavoriteService(
    private val favoritePort: FavoritePort,
) : ToggleFavoriteUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == command.userId")
    override fun toggle(command: ToggleFavoriteCommand): ToggleFavoriteResult {
        val existingFavorite = favoritePort.findByUserIdAndActivityId(command.userId, command.activityId)

        return existingFavorite?.let { favorite ->
            // 즐겨찾기가 이미 존재하면(null이 아니면) 삭제
            favoritePort.delete(favorite)
            ToggleFavoriteResult(
                activityId = command.activityId,
                isFavorited = false
            )
        } ?: run {
            // 즐겨찾기가 없으면(null이면) 새로 생성하여 저장
            val newFavorite = Favorite(
                userId = command.userId,
                activityId = command.activityId
            )
            favoritePort.save(newFavorite)
            ToggleFavoriteResult(
                activityId = command.activityId,
                isFavorited = true
            )
        }
    }
}
