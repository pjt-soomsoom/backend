package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded.AchievementRewardEmbeddable
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded.DisplayInfoEmbeddable
import com.soomsoom.backend.adapter.out.persistence.achievement.toEmbeddable
import com.soomsoom.backend.adapter.out.persistence.achievement.toEntity
import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "achievements",
    indexes = [
        Index(name = "idx_achievements_deleted_at", columnList = "deleted_at"),
        Index(name = "idx_achievements_category", columnList = "category")
    ]
)
class AchievementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    var name: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "titleTemplate", column = Column(name = "unlocked_title_template")),
        AttributeOverride(name = "bodyTemplate", column = Column(name = "unlocked_body_template"))
    )
    var unlockedDisplayInfo: DisplayInfoEmbeddable,

    var phrase: String?,

    @Enumerated(EnumType.STRING)
    var grade: AchievementGrade,

    @Enumerated(EnumType.STRING)
    var category: AchievementCategory,

    @Embedded
    var reward: AchievementRewardEmbeddable?,

    @OneToMany(
        mappedBy = "achievement",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var conditions: MutableList<AchievementConditionJpaEntity> = mutableListOf(),

) : BaseTimeEntity() {
    fun update(achievement: Achievement) {
        // 'id'는 변경되지 않으므로 업데이트 로직에서 제외합니다.
        this.name = achievement.name
        this.phrase = achievement.phrase
        this.grade = achievement.grade
        this.category = achievement.category
        this.deletedAt = achievement.deletedAt

        // Embeddable 객체는 새로운 인스턴스로 교체합니다.
        this.unlockedDisplayInfo = achievement.unlockedDisplayInfo.toEmbeddable()
        this.reward = achievement.reward?.toEmbeddable()

        this.conditions.clear()
        achievement.conditions.forEach { domainCondition ->
            val conditionEntity = domainCondition.toEntity()
            this.addCondition(conditionEntity)
        }
    }

    fun addCondition(condition: AchievementConditionJpaEntity) {
        this.conditions.add(condition)
        condition.achievement = this
    }
}
