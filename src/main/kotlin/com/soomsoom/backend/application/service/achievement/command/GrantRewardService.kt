package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.application.port.`in`.user.command.AddUserPointsCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddUserPointsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GrantRewardService(
    private val achievementPort: AchievementPort,
    private val addUserPointsUseCase: AddUserPointsUseCase,
) : GrantRewardUseCase {
    override fun grantReward(userId: Long, achievementId: Long) {
        val achievement = achievementPort.findById(achievementId)
            ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

        achievement.rewardPoints?.let { points ->
            if (points > 0) {
                val command = AddUserPointsCommand(userId = userId, amount = points)
                addUserPointsUseCase.add(command)
            }
        }

        achievement.rewardItemId?.let { itemId ->
            /* TODO: 아이템 지급 로직 */
        }
    }
}
