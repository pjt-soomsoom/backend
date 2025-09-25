package com.soomsoom.backend.application.port.out.mission

import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import java.time.LocalDateTime

interface MissionCompletionLogPort {
    fun save(log: MissionCompletionLog): MissionCompletionLog

    /**
     * 특정 사용자가 특정 미션을 주어진 시간 범위 내에 '달성'한 기록이 있는지 확인합니다.
     *
     * @param userId 확인할 사용자의 ID
     * @param missionId 확인할 미션의 ID
     * @param from 시작 시간 (LocalDateTime)
     * @param to 종료 시간 (LocalDateTime)
     * @return 주어진 시간 범위 내에 달성 기록이 존재하면 true, 아니면 false
     */
    fun existsByCompletedAtBetween(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): Boolean

    /**
     * 특정 사용자가 주어진 시간 범위 내에 '달성'했지만 아직 '보상'받지 않은 로그를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param missionId 조회할 미션의 ID
     * @param from 시작 시간 (LocalDateTime)
     * @param to 종료 시간 (LocalDateTime)
     */
    fun findCompletedButUnrewardedLog(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): MissionCompletionLog?
    fun exists(userId: Long, missionId: Long): Boolean

    fun existsWithUnrewarded(userId: Long, missionId: Long): Boolean
}
