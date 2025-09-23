package com.soomsoom.backend.application.service.adrewardlog

import com.soomsoom.backend.application.port.`in`.adrewardlog.command.VerifyAdRewardCommand
import com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command.VerifyAdRewardUseCase
import com.soomsoom.backend.application.port.out.adrewardlog.AdMobVerificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.RewardSourcePayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.adrewardlog.AdRewardLogErrorCode
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.reward.model.RewardSource
import com.soomsoom.backend.domain.reward.model.RewardType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class VerifyAdRewardService(
    private val adMobVerificationPort: AdMobVerificationPort,
    private val adRewardLogPort: com.soomsoom.backend.application.port.out.adrewardlog.AdRewardLogPort,
    private val dateHelper: DateHelper,
    private val eventPublisher: ApplicationEventPublisher,

    @Value("\${reward-ad.base-path}")
    private val basePath: String,
) : VerifyAdRewardUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun command(command: VerifyAdRewardCommand) {
        val finalAdUnitId = if (command.adUnitId.startsWith(basePath)) {
            command.adUnitId
        } else {
            "$basePath/${command.adUnitId}"
        }

        val isGoogleRequest = adMobVerificationPort.verify(command.fullCallbackUrl)
        if (!isGoogleRequest) {
            logger.warn("Invalid AdMob SSV callback signature received. URL: ${command.fullCallbackUrl}")
            throw SecurityException("Invalid AdMob SSV callback signature.")
        }

        val isTransactionDuplicated = adRewardLogPort.existsByTransactionId(command.transactionId)
        if (isTransactionDuplicated) {
            logger.info("Duplicate transaction ignored: ${command.transactionId}")
            return
        }

        val userId = command.userId.toLong()
        val rewardAmount = command.rewardAmount.toInt()

        val businessDay = dateHelper.getBusinessDay(LocalDateTime.now())
        val hasAlreadyReceivedToday = adRewardLogPort.existsByUserIdAndAdUnitIdAndCreatedAtBetween(
            userId,
            finalAdUnitId,
            businessDay.start,
            businessDay.end
        )
        if (hasAlreadyReceivedToday) {
            throw SoomSoomException(AdRewardLogErrorCode.AD_REWARD_ALREADY_GRANTED)
        }

        // 보상 '기록'
        val newAdRewardLog = com.soomsoom.backend.domain.adrewardlog.model.AdRewardLog.create(
            userId = userId,
            adUnitId = finalAdUnitId,
            transactionId = command.transactionId,
            amount = Points(rewardAmount)
        )
        adRewardLogPort.save(newAdRewardLog)
        logger.info("Ad reward log created successfully for userId: $userId, adUnitId: $finalAdUnitId")

        // 보상 지급 요청 이벤트를 발행
        val event = Event(
            eventType = EventType.REWARD_SOURCE_TRIGGERED,
            payload = RewardSourcePayload(
                userId = userId,
                points = rewardAmount,
                itemId = null,
                source = RewardSource.AD_WATCH,
                notificationTitle = "보상 획득!",
                notificationBody = "광고 시청으로 하트를 획득했어요!",
                notificationImage = null,
                sendNotification = true,
                rewardType = RewardType.POINT
            )
        )
        eventPublisher.publishEvent(event)
    }
}
