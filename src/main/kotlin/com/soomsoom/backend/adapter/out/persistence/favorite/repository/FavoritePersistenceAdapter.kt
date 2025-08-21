package com.soomsoom.backend.adapter.out.persistence.favorite.repository

import com.soomsoom.backend.adapter.out.persistence.activity.toDomain
import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.FavoriteJpaRepository
import com.soomsoom.backend.adapter.out.persistence.favorite.repository.jpa.FavoriteQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.favorite.toDomain
import com.soomsoom.backend.adapter.out.persistence.favorite.toEntity
import com.soomsoom.backend.application.port.out.favorite.FavoritePort
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.favoriote.model.Favorite
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FavoritePersistenceAdapter(
    private val favoriteJpaRepository: FavoriteJpaRepository,
    private val favoriteQueryDslRepository: FavoriteQueryDslRepository,
) : FavoritePort {
    override fun findByUserIdAndActivityId(userId: Long, activityId: Long): Favorite? {
        return favoriteJpaRepository.findByUserIdAndActivityId(userId, activityId)?.toDomain()
    }

    override fun save(favorite: Favorite): Favorite {
        return favoriteJpaRepository.save(favorite.toEntity()).toDomain()
    }

    override fun delete(favorite: Favorite) {
        favoriteJpaRepository.delete(favorite.toEntity())
    }

    override fun findFavoriteActivities(userId: Long, pageable: Pageable): Page<Activity> {
        return favoriteQueryDslRepository.findFavoriteActivities(userId, pageable)
            .map { it.toDomain() }
    }
}
