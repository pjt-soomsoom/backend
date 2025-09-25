package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "missions",
    indexes = [Index(name = "idx_mission_type_deleted_at", columnList = "type, deleted_at")]
)
class MissionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: MissionType,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var targetValue: Int,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "title", column = Column(name = "completion_notification_title", nullable = false)),
        AttributeOverride(name = "body", column = Column(name = "completion_notification_body", nullable = false))
    )
    var completionNotification: NotificationContentEmbeddable,

    @Embedded
    var reward: MissionRewardEmbeddable,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var repeatableType: RepeatableType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var claimType: ClaimType,
) : BaseTimeEntity()
