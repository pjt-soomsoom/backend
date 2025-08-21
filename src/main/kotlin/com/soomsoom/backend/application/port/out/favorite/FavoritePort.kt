package com.soomsoom.backend.application.port.out.favorite

import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.favoriote.model.Favorite
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FavoritePort {
    fun findByUserIdAndActivityId(userId: Long, activityId: Long): Favorite?
    fun save(favorite: Favorite): Favorite
    fun delete(favorite: Favorite)
    fun findFavoriteActivities(userId: Long, pageable: Pageable): Page<Activity>
}
