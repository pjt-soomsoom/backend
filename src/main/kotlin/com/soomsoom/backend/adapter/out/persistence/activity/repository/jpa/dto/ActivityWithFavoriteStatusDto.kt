package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity

data class ActivityWithFavoriteStatusDto @QueryProjection constructor(
    val activity: ActivityJpaEntity,
    val isFavorited: Boolean,
)
