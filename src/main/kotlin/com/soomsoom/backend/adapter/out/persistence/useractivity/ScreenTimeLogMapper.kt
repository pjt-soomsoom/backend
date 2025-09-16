package com.soomsoom.backend.adapter.out.persistence.useractivity

import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.ScreenTimeLogJpaEntity
import com.soomsoom.backend.domain.useractivity.model.aggregate.ScreenTimeLog

fun ScreenTimeLog.toEntity(): ScreenTimeLogJpaEntity {
    return ScreenTimeLogJpaEntity(
        id = this.id ?: 0L,
        userId = this.userId,
        durationInSeconds = this.durationInSeconds
    )
}

fun ScreenTimeLogJpaEntity.toDomain(): ScreenTimeLog {
    return ScreenTimeLog(
        id = this.id,
        userId = this.userId,
        durationInSeconds = this.durationInSeconds,
        createdAt = this.createdAt!!
    )
}
