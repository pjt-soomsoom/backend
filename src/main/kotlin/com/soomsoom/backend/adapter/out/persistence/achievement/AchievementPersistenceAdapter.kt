package com.soomsoom.backend.adapter.out.persistence.achievement

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementConditionJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.AchievementQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.UserAchievedJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.UserProgressJpaRepository
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto.AchievementWithProgressDto
import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserAchievedPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.achievement.dto.AchievementDetailsDto
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import com.soomsoom.backend.domain.common.DeletionStatus
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
    override fun findById(achievementId: Long, deletionStatus: DeletionStatus): Achievement? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> achievementJpaRepository.findByIdAndDeletedAtIsNull(achievementId)
            DeletionStatus.DELETED -> achievementJpaRepository.findByIdAndDeletedAtIsNotNull(achievementId)
            DeletionStatus.ALL -> achievementJpaRepository.findByIdOrNull(achievementId)
        }
        return entity?.let { achievementEntity ->
            val conditions = conditionJpaRepository.findByAchievementId(achievementEntity.id)

            // 3. 엔티티를 도메인 객체로 변환하고, 조회된 조건들을 주입
            achievementEntity.toDomain().apply {
                this.conditions = conditions.map { it.toDomain() }
            }
        }
    }

    override fun findConditionsByAchievementId(achievementId: Long): List<AchievementCondition> {
        return conditionJpaRepository.findByAchievementId(achievementId).map { it.toDomain() }
    }

    override fun findConditionsByType(type: ConditionType): List<AchievementCondition> {
        return conditionJpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun findUnachievedConditionsByType(userId: Long, type: ConditionType): List<AchievementCondition> {
        return queryDslRepository.findUnachievedConditionsByType(userId, type).map { it.toDomain() }
    }

    override fun findAchievementsWithProgress(criteria: FindMyAchievementsCriteria, pageable: Pageable): Page<AchievementDetailsDto> {
        val resultPage: Page<AchievementWithProgressDto> = queryDslRepository.findAchievementsWithProgress(criteria, pageable)

        return resultPage.map { dto ->
            AchievementDetailsDto(
                achievement = dto.achievement.toDomain(),
                userAchieved = dto.userAchieved?.toDomain(),
                userProgress = dto.userProgress?.toDomain(),
                targetValue = dto.targetValue
            )
        }
    }

    override fun findNewlyAchievableEntities(userId: Long, type: ConditionType): List<Achievement> {
        return queryDslRepository.findNewlyAchievableEntities(userId, type).map { it.toDomain() }
    }

    override fun save(achievement: Achievement): Achievement {
        val entity = if (achievement.id == 0L) {
            achievement.toEntity()
        } else {
            val existingEntity = achievementJpaRepository.findByIdOrNull(achievement.id)
                ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

            existingEntity.update(achievement)
            existingEntity
        }
        val savedEntity = achievementJpaRepository.save(entity)

        return savedEntity.toDomain().apply {
            this.conditions = achievement.conditions
        }
    }

    override fun delete(achievement: Achievement) {
        save(achievement)
    }

    override fun findAll(criteria: FindAllAchievementsCriteria, pageable: Pageable): Page<Achievement> {
        // 페이징된 업적 엔티티 목록을 먼저 조회
        val achievementEntitiesPage = queryDslRepository.findAll(criteria, pageable)
        val achievementIds = achievementEntitiesPage.content.map { it.id }

        // 조회된 업적 ID들에 해당하는 모든 조건들을 한 번의 쿼리로 조회
        val conditions = queryDslRepository.findConditionsIn(achievementIds)

        // 조건들을 achievementId 기준으로 그룹핑하여 Map으로 변환 (처리 효율성 증가)
        val conditionsMap = conditions.groupBy { it.achievement }

        // 업적 엔티티를 도메인 객체로 변환하면서, Map을 사용해 각 업적에 맞는 조건 목록을 주입
        return achievementEntitiesPage.map { entity ->
            val achievementConditions = conditionsMap[entity]?.map { it.toDomain() } ?: emptyList()
            entity.toDomain().apply {
                this.conditions = achievementConditions
            }
        }
    }

    // [ADMIN] saveConditions (자식 엔티티 저장)
    override fun saveConditions(conditions: List<AchievementCondition>): List<AchievementCondition> {
        val entities = conditions.map { it.toEntity() }
        val savedEntities = conditionJpaRepository.saveAll(entities)
        return savedEntities.map { it.toDomain() }
    }

    override fun deleteConditionsByAchievementId(achievementId: Long) {
        conditionJpaRepository.deleteAllByAchievementId(achievementId)
    }

    // UserAchievedPort 구현
    override fun exists(userId: Long, achievementId: Long): Boolean {
        return userAchievedJpaRepository.existsByUserIdAndAchievementId(userId, achievementId)
    }

    override fun save(achieved: UserAchieved): UserAchieved {
        return userAchievedJpaRepository.save(achieved.toEntity()).toDomain()
    }

    override fun deleteUserAchievedByUserId(userId: Long) {
        userAchievedJpaRepository.deleteAllByUserId(userId)
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

    override fun deleteUserProgressByUserId(userId: Long) {
        userProgressJpaRepository.deleteAllByUserId(userId)
    }
}
