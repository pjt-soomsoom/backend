package com.soomsoom.backend.adapter.out.persistence.common.entity

import jakarta.persistence.Embeddable

@Embeddable
data class PointsEmbeddable(
    val value: Int,
)
