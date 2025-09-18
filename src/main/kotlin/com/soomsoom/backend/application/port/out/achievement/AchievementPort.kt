package com.soomsoom.backend.application.port.out.achievement

import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.application.port.out.achievement.dto.AchievementDetailsDto
import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AchievementPort {
    /**
     * ID로 업적의 기본 정보를 조회
     */
    fun findById(achievementId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Achievement?

    /**
     * 특정 업적에 속한 모든 달성 조건 목록을 조회
     */
    fun findConditionsByAchievementId(achievementId: Long): List<AchievementCondition>

    /**
     * 특정 행동 타입(ConditionType)을 조건으로 갖는 모든 조건 목록을 조회
     * (예: DIARY_STREAK 타입을 가진 모든 조건들을 찾아, 일기 연속 작성 시 어떤 업적들을 체크해야 하는지 확인)
     */
    fun findConditionsByType(type: ConditionType): List<AchievementCondition>

    /**
     * 특정 행동 타입(ConditionType)을 조건으로 갖으며, 아직 달성되지 않은 업적 목록을 조회
     */
    fun findUnachievedConditionsByType(userId: Long, type: ConditionType): List<AchievementCondition>

    /**
     * 사용자의 업적 현황(업적 정보, 달성 여부, 진행도)을 조회
     */
    fun findAchievementsWithProgress(criteria: FindMyAchievementsCriteria, pageable: Pageable): Page<AchievementDetailsDto>

    /**
     * 새로 달성 가능한 업적이 있는지 조회
     */
    fun findNewlyAchievableEntities(userId: Long, type: ConditionType): List<Achievement>

    /**
     * [ADMIN] 업적 저장 (생성/수정)
     */
    fun save(achievement: Achievement): Achievement

    /**
     * [ADMIN] 업적 ID로 삭제
     */
    fun delete(achievement: Achievement)

    /**
     * [ADMIN] 모든 업적 목록 조회 (페이징)
     */
    fun findAll(criteria: FindAllAchievementsCriteria, pageable: Pageable): Page<Achievement>

    /**
     * [ADMIN] 여러 개의 달성 조건을 한 번에 저장
     */
    fun saveConditions(conditions: List<AchievementCondition>): List<AchievementCondition>

    /**
     * [ADMIN] 특정 업적에 속한 모든 달성 조건들을 삭제
     */
    fun deleteConditionsByAchievementId(achievementId: Long)
}
