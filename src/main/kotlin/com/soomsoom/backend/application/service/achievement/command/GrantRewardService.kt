package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GrantRewardService(
    private val achievementPort: AchievementPort,
) : GrantRewardUseCase {
    override fun grandReward(userId: Long, achievementId: Long) {
        val achievement = achievementPort.findById(achievementId)
            ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

        achievement.rewardPoints?.let { points ->
            if (points > 0) {
                /* TODO: 포인트 지급 로직 */
            }
        }

        achievement.rewardItemId?.let { itemId ->
            /* TODO: 아이템 지급 로직 */
        }
    }
}
