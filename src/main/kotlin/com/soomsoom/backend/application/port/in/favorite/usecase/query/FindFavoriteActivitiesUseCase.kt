package com.soomsoom.backend.application.port.`in`.favorite.usecase.query

import com.soomsoom.backend.application.port.`in`.favorite.dto.FavoriteActivityResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindFavoriteActivitiesUseCase {
    fun find(userId: Long, pageable: Pageable): Page<FavoriteActivityResult>
}
