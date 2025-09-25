package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.command.ProcessMissionProgressCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ProcessMissionProgressUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.application.service.mission.strategy.MissionProcessingStrategyLocator
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.event.payload.PageVisitedPayload
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.stereotype.Service

@Service
class ProcessMissionProgressService(
    private val missionPort: MissionPort,
    private val strategyLocator: MissionProcessingStrategyLocator,
) : ProcessMissionProgressUseCase {
    override fun process(command: ProcessMissionProgressCommand) {
        val payload = command.payload

        // 발생한 이벤트(Payload)를 처리할 수 있는 MissionType 목록을 결정
        val relevantMissionTypes = mapPayloadToMissionTypes(payload)
        if (relevantMissionTypes.isEmpty()) return

        // 해당 MissionType을 가진 모든 활성 미션을 DB에서 조회
        val missions = missionPort.findAllByTypes(relevantMissionTypes)
        if (missions.isEmpty()) return

        // 조회된 각각의 미션에 대해...
        missions.forEach { mission ->
            // 해당 미션의 MissionType에 맞는 Strategy를 찾아서,
            val strategy = strategyLocator.findStrategy(mission.type)

            // 'mission' 데이터와 함께 로직을 실행
            strategy?.process(mission, payload)
        }
    }

    private fun mapPayloadToMissionTypes(payload: Payload): List<MissionType> {
        return when (payload) {
            is UserAuthenticatedPayload -> listOf(MissionType.CONSECUTIVE_ATTENDANCE)
            is ActivityCompletedPayload -> when (payload.activityType) {
                ActivityType.BREATHING -> listOf(
                    MissionType.DAILY_BREATHING_COUNT,
                    MissionType.FIRST_EVER_BREATHING
                )
                else -> emptyList()
            }
            is PageVisitedPayload -> listOf(MissionType.FIRST_PAGE_VISIT)
            else -> emptyList()
        }
    }
}
