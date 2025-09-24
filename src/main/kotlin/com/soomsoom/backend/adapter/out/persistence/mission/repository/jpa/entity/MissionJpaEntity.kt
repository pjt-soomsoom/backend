package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "missions")
class MissionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    var type: MissionType,
    var title: String,
    var description: String,
    var targetValue: Int,
    @Embedded
    var completionNotification: NotificationContentEmbeddable,
    @Embedded
    var reward: MissionRewardEmbeddable,
    @Enumerated(EnumType.STRING)
    var repeatableType: RepeatableType,
    @Enumerated(EnumType.STRING)
    var claimType: ClaimType,
) : BaseTimeEntity()
