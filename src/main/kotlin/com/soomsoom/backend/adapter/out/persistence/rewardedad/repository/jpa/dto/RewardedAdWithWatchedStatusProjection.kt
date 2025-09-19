package com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection

data class RewardedAdWithWatchedStatusProjection @QueryProjection constructor(
    val id: Long,
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
    val watched: Boolean,
)
