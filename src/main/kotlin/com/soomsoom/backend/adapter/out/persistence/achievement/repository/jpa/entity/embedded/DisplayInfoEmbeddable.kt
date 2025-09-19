package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
data class DisplayInfoEmbeddable(
    var titleTemplate: String,
    var bodyTemplate: String,
)
