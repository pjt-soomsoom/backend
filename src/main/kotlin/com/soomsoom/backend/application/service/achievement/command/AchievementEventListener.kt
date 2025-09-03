package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateUserProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.event.payload.UserPlayTimeAccumulatedPayload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProgressUpdateEventListener(
    private val updateUserProgressUseCase: UpdateUserProgressUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener
    fun handleDiaryCreatedEvent(event: Event<DiaryCreatedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    /**
     * ActivityCompleted 이벤트를 직접 구독하는 리스너
     */
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener
    fun handleActivityCompletedEvent(event: Event<ActivityCompletedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener
    fun handleUserPlayTimeAccumulatedEvent(event: Event<UserPlayTimeAccumulatedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }
}

@Component
class RewardEventListener(
    private val grantRewardUseCase: GrantRewardUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener
    fun handleAchievementAchievedEvent(event: Event<AchievementAchievedNotificationPayload>) {
        grantRewardUseCase.grantReward(event.payload.userId, event.payload.achievementId)
    }
}
