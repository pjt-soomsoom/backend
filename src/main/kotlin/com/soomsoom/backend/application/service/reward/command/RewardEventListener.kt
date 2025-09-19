package com.soomsoom.backend.application.service.reward.command

import com.soomsoom.backend.application.port.`in`.reward.command.GrantRewardCommand
import com.soomsoom.backend.application.port.`in`.reward.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.RewardSourcePayload
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class RewardEventListener(
    private val grantRewardUseCase: GrantRewardUseCase,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @TransactionalEventListener(
        value = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).REWARD_SOURCE_TRIGGERED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Order(20)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleRewardSourceEvent(event: Event<RewardSourcePayload>) {
        val payload = event.payload
        logger.info("Received REWARD_SOURCE_TRIGGERED event for userId=${payload.userId} from source='${payload.source}'")

        try {
            val command = GrantRewardCommand(
                userId = payload.userId,
                rewardType = payload.rewardType,
                points = payload.points,
                itemId = payload.itemId,
                source = payload.source,
                sendNotification = payload.sendNotification,
                notificationTitle = payload.notificationTitle,
                notificationBody = payload.notificationBody,
                notificationImage = payload.notificationImage
            )
            grantRewardUseCase.command(command)
        } catch (e: Exception) {
            logger.error("Failed to process reward event for userId=${payload.userId}. Error: ${e.message}", e)
        }
    }
}
