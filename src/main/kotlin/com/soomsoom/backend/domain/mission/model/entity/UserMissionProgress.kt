package com.soomsoom.backend.domain.mission.model.entity

import com.soomsoom.backend.domain.mission.model.vo.MissionProgressData
import java.time.LocalDateTime

/**
 * 시간의 흐름에 따라 상태가 변하는 복잡한 미션(예: 연속 출석)의 '진행 과정'을 기록하는 엔티티.
 * 단순한 미션(예: 첫 방문)은 이 엔티티를 사용하지 않을 수 있음.
 *
 * @property id 진행 기록의 고유 식별자입니다.
 * @property userId 미션을 진행 중인 사용자의 ID입니다.
 * @property missionId 진행 중인 미션의 ID입니다.
 * @property progressData 미션 진행 상태를 나타내는 타입-세이프한 데이터 객체입니다.
 * @property updatedAt 이 진행 정보가 마지막으로 갱신된 시간입니다.
 */
class UserMissionProgress(
    val id: Long = 0,
    val userId: Long,
    val missionId: Long,
    var progressData: MissionProgressData,
    var updatedAt: LocalDateTime,
)
