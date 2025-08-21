package com.soomsoom.backend.application.service.favorite.query

import com.soomsoom.backend.application.port.`in`.favorite.dto.FavoriteActivityResult
import com.soomsoom.backend.application.port.`in`.favorite.usecase.query.FindFavoriteActivitiesUseCase
import com.soomsoom.backend.application.port.out.favorite.FavoritePort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindFavoriteActivitiesService(
    private val favoritePort: FavoritePort,
) : FindFavoriteActivitiesUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    override fun find(userId: Long, pageable: Pageable): Page<FavoriteActivityResult> {
        val activityPage = favoritePort.findFavoriteActivities(userId, pageable)
        return activityPage.map { FavoriteActivityResult.from(it) }
    }
}
