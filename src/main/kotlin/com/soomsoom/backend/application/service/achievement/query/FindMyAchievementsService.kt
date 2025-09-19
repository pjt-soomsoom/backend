package com.soomsoom.backend.application.service.achievement.query

import com.soomsoom.backend.application.helper.AchievementDisplayHelper
import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindMyAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindMyAchievementsService(
    private val achievementPort: AchievementPort,
    private val displayHelper: AchievementDisplayHelper,
) : FindMyAchievementsUseCase {

    @PreAuthorize("hasRole('ADMIN') or (#criteria.userId == authentication.principal.id and #criteria.deletionStatus.name() == 'ACTIVE')")
    override fun find(criteria: FindMyAchievementsCriteria, pageable: Pageable): Page<FindMyAchievementsResult> {
        val achievementWithProgressPage = achievementPort.findAchievementsWithProgress(criteria, pageable)

        return achievementWithProgressPage.map { dto ->
            val achievement = dto.achievement
            val condition = achievement.conditions.first() // 대표 조건

            // 1. 헬퍼를 사용하여 동적 설명 생성
            val description = displayHelper.formatDescription(condition.type, condition.targetValue)

            // 2. 헬퍼를 사용하여 진행도 정보 생성
            val progressInfo = displayHelper.createProgressInfo(
                type = condition.type,
                targetValue = condition.targetValue,
                // 달성했다면 목표치와 현재치를 동일하게, 아니면 실제 진행도를 사용
                currentValue = if (dto.userAchieved != null) condition.targetValue else dto.userProgress?.currentValue
            )

            // 3. 최종 DTO 조립
            FindMyAchievementsResult.of(
                achievement = achievement,
                userAchieved = dto.userAchieved,
                progressInfo = progressInfo,
                description = description
            )
        }
    }
}
