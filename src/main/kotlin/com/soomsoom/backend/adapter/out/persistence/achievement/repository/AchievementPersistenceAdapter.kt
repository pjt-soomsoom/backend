package com.soomsoom.backend.adapter.out.persistence.achievement.repository

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementConditionJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.UserAchievedJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.UserProgressJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.AchievementWithProgressDto
import com.soomsoom.backend.adapter.out.persistence.achievement.toDomain
import com.soomsoom.backend.adapter.out.persistence.achievement.toEntity
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserAchievedPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.achievement.dto.AchievementDetailsDto
import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserAchieved
import com.soomsoom.backend.domain.achievement.model.UserProgress
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class AchievementPersistenceAdapter(
    private val achievementJpaRepository: AchievementJpaRepository,
    private val conditionJpaRepository: AchievementConditionJpaRepository,
    private val userAchievedJpaRepository: UserAchievedJpaRepository,
    private val userProgressJpaRepository: UserProgressJpaRepository,
    private val queryDslRepository: AchievementQueryDslRepository,
) : AchievementPort, UserAchievedPort, UserProgressPort {

    // AchievementPort 구현
    override fun findById(achievementId: Long): Achievement? {
        return achievementJpaRepository.findByIdOrNull(achievementId)?.toDomain()
    }

    override fun findConditionsByAchievementId(achievementId: Long): List<AchievementCondition> {
        return conditionJpaRepository.findByAchievementId(achievementId).map { it.toDomain() }
    }

    override fun findConditionsByType(type: ConditionType): List<AchievementCondition> {
        return conditionJpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun findAchievementsWithProgress(userId: Long, pageable: Pageable, statusFilter: AchievementStatusFilter): Page<AchievementDetailsDto> {
        val resultPage: Page<AchievementWithProgressDto> = queryDslRepository.findAchievementsWithProgress(userId, pageable, statusFilter)

        return resultPage.map { dto ->
            AchievementDetailsDto(
                achievement = dto.achievement.toDomain(),
                userAchieved = dto.userAchieved?.toDomain(),
                userProgress = dto.userProgress?.toDomain(),
                targetValue = dto.targetValue
            )
        }
    }

    override fun findNewlyAchievableIds(userId: Long, type: ConditionType): List<Long> {
        return queryDslRepository.findNewlyAchievableIds(userId, type)
    }

    // UserAchievedPort 구현
    override fun exists(userId: Long, achievementId: Long): Boolean {
        return userAchievedJpaRepository.existsByUserIdAndAchievementId(userId, achievementId)
    }

    override fun save(achieved: UserAchieved): UserAchieved {
        return userAchievedJpaRepository.save(achieved.toEntity()).toDomain()
    }

    // UserProgressPort 구현
    override fun findByUserIdAndType(userId: Long, type: ConditionType): UserProgress? {
        return userProgressJpaRepository.findByUserIdAndType(userId, type)?.toDomain()
    }

    override fun findByUserIdAndTypes(userId: Long, types: List<ConditionType>): List<UserProgress> {
        return userProgressJpaRepository.findByUserIdAndTypeIn(userId, types).map { it.toDomain() }
    }

    override fun save(progress: UserProgress): UserProgress {
        val entity = userProgressJpaRepository.findByUserIdAndType(progress.userId, progress.type)
            ?.apply { this.currentValue = progress.currentValue }
            ?: progress.toEntity()
        return userProgressJpaRepository.save(entity).toDomain()
    }
}
