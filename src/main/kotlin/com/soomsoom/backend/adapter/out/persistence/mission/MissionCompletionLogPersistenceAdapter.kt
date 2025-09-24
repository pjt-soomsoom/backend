package com.soomsoom.backend.adapter.out.persistence.mission

import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MissionCompletionLogPersistenceAdapter() : MissionCompletionLogPort {
    override fun save(log: MissionCompletionLog): MissionCompletionLog {
        TODO("Not yet implemented")
    }

    override fun existsByCompletedAtBetween(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): Boolean {
        TODO("Not yet implemented")
    }

    override fun findCompletedButUnrewardedLog(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): MissionCompletionLog? {
        TODO("Not yet implemented")
    }

    override fun existsBy(userId: Long, missionId: Long): Boolean {
        TODO("Not yet implemented")
    }
}
