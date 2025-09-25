package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import jakarta.persistence.Embeddable

@Embeddable
class NotificationContentEmbeddable(
    var title: String,
    var body: String,
)
