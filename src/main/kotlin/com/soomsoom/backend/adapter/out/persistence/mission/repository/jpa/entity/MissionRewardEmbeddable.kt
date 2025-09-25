package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded

@Embeddable
class MissionRewardEmbeddable(
    var points: Int?,
    var itemId: Long?,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "title", column = Column(name = "reward_notification_title")),
        AttributeOverride(name = "body", column = Column(name = "reward_notification_body"))
    )
    var notification: NotificationContentEmbeddable,
)
