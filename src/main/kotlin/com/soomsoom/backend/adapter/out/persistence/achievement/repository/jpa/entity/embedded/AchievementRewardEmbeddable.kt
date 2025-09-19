package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded

@Embeddable
data class AchievementRewardEmbeddable(
    var rewardPoints: Int?,
    var rewardItemId: Long?,

    @Embedded
    @AttributeOverrides(
        // DB 컬럼명을 재정의하여 필드명 충돌 방지
        AttributeOverride(name = "titleTemplate", column = Column(name = "reward_title_template")),
        AttributeOverride(name = "bodyTemplate", column = Column(name = "reward_body_template"))
    )
    var rewardDisplayInfo: DisplayInfoEmbeddable,
)
