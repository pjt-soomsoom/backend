package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.domain.mission.model.enums.MissionType
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*

@Component
class MissionProcessingStrategyLocator(
    private val strategies: List<MissionProcessingStrategy>,
) {
    // MissionType을 키로, Strategy 구현체를 값으로 갖는 Map.
    private lateinit var strategyMap: EnumMap<MissionType, MissionProcessingStrategy>

    /**
     * 의존성 주입이 완료된 후, 주입받은 Strategy 리스트를 Map 형태로 변환하여 초기화
     * 이 작업은 애플리케이션 시작 시 단 한 번만 수행됩니다.
     */
    @PostConstruct
    fun initialize() {
        strategyMap = EnumMap(MissionType::class.java)
        strategies.forEach { strategy ->
            strategyMap[strategy.getMissionType()] = strategy
        }
    }

    /**
     * 주어진 MissionType에 해당하는 Strategy 구현체를 Map에서 찾아 반환
     *
     * @param missionType 찾고자 하는 미션의 타입
     * @return 해당 MissionType을 처리하는 Strategy. 없으면 null을 반환합니다.
     */
    fun findStrategy(missionType: MissionType): MissionProcessingStrategy? {
        return strategyMap[missionType]
    }
}
