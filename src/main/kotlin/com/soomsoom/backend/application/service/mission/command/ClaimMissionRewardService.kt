package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.command.ClaimMissionRewardCommand
import com.soomsoom.backend.application.port.`in`.mission.dto.ClaimMissionRewardResult
import com.soomsoom.backend.application.port.`in`.mission.dto.MissionRewardUserDto
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ClaimMissionRewardUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.RewardSourcePayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.mission.MissionErrorCode
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.reward.model.RewardSource
import com.soomsoom.backend.domain.reward.model.RewardType
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 사용자가 미션 보상을 수령하는 유스케이스의 구현체
 */
@Service
@Transactional
class ClaimMissionRewardService(
    private val missionPort: MissionPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val dateHelper: DateHelper,
    private val itemPort: ItemPort,
) : ClaimMissionRewardUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun claimReward(command: ClaimMissionRewardCommand): ClaimMissionRewardResult {
        val mission = missionPort.findById(command.missionId)
            ?: throw SoomSoomException(MissionErrorCode.NOT_FOUND)

        val businessDay = dateHelper.getBusinessDay(LocalDateTime.now())

        logger.info("userId = {}, missionId = {}", command.userId, command.missionId)
        val log = missionCompletionLogPort.findCompletedButUnrewardedLog(
            userId = command.userId,
            missionId = command.missionId,
            from = businessDay.start,
            to = businessDay.end
        ) ?: throw SoomSoomException(MissionErrorCode.NOT_COMPLETED)

        logger.info("log = {}", log)

        // MissionReward VO로부터 RewardType을 결정
        val rewardType = when {
            mission.reward.points != null -> RewardType.POINT
            mission.reward.itemId != null -> RewardType.ITEM
            else -> throw IllegalStateException("미션에 보상이 설정되지 않았습니다.") // 발생해서는 안 되는 예외
        }

        val itemImage: String? = mission.reward.itemId?.let {
            itemPort.findById(it)?.imageUrl ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val rewardEvent = Event(
            eventType = EventType.REWARD_SOURCE_TRIGGERED,
            payload = RewardSourcePayload(
                userId = command.userId,
                rewardType = rewardType,
                points = mission.reward.points,
                itemId = mission.reward.itemId,
                source = RewardSource.MISSION,
                sendNotification = mission.claimType == ClaimType.AUTOMATIC,
                notificationTitle = mission.reward.notification.title,
                notificationBody = mission.reward.notification.title,
                notificationImage = itemImage
            )
        )

        eventPublisher.publishEvent(rewardEvent)

        log.markAsRewarded()
        missionCompletionLogPort.save(log)

        return ClaimMissionRewardResult(
            claimedReward = MissionRewardUserDto.from(mission.reward)
        )
    }
}
