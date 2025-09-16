package com.soomsoom.backend.adapter.out.persistence.useractive // package com.soomsoom.backend.adapter.out.persistence.connectionlog

import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.ConnectionLogJpaRepository
import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.ConnectionLogQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.application.port.out.useractivity.ConnectionLogPort
import com.soomsoom.backend.domain.useractivity.model.aggregate.ConnectionLog
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ConnectionLogPersistenceAdapter(
    private val connectionLogJpaRepository: ConnectionLogJpaRepository,
    private val connectionLogQueryDslRepository: ConnectionLogQueryDslRepository,
) : ConnectionLogPort {
    override fun existsByUserIdAndCreatedAtBetween(userId: Long, from: LocalDateTime, to: LocalDateTime): Boolean {
        return connectionLogQueryDslRepository.existsByUserIdAndCreatedAtBetween(userId, from, to)
    }

    override fun save(connectionLog: ConnectionLog): ConnectionLog {
        return connectionLogJpaRepository.save(connectionLog.toEntity()).toDomain()
    }

    override fun findInactiveUsers(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto> {
        return connectionLogQueryDslRepository.findInactiveUsers(inactivityConditions, pageNumber, pageSize)
    }
}
