package com.soomsoom.backend.adapter.out.persistence.useractivity

import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.ScreenTimeLogJpaRepository
import com.soomsoom.backend.application.port.out.useractivity.ScreenTimeLogPort
import com.soomsoom.backend.domain.useractivity.model.aggregate.ScreenTimeLog
import org.springframework.stereotype.Component

@Component
class ScreenTimeLogPersistenceAdapter(
    private val screenTimeLogJpaRepository: ScreenTimeLogJpaRepository,
) : ScreenTimeLogPort {
    override fun save(screenTimeLog: ScreenTimeLog): ScreenTimeLog {
        return screenTimeLogJpaRepository.save(screenTimeLog.toEntity()).toDomain()
    }
}
