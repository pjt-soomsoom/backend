package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateUserProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ProgressUpdateEventListener(
    private val updateUserProgressUseCase: UpdateUserProgressUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @EventListener
    fun handleEvent(event: Event<Payload>) {
        if (event.payload is AchievementAchievedNotificationPayload) return
        updateUserProgressUseCase.updateProgress(event)
    }
}

@Component
class RewardEventListener(
    private val grantRewardUseCase: GrantRewardUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @EventListener
    fun handleAchievementAchievedEvent(event: Event<AchievementAchievedNotificationPayload>) {
        grantRewardUseCase.grantReward(event.payload.userId, event.payload.achievementId)
    }
}
