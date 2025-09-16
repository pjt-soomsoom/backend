package com.soomsoom.backend.adapter.out.persistence.useractive // package com.soomsoom.backend.adapter.out.persistence.connectionlog

import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.entity.ConnectionLogJpaEntity
import com.soomsoom.backend.domain.useractivity.model.aggregate.ConnectionLog

fun ConnectionLog.toEntity(): ConnectionLogJpaEntity {
    return ConnectionLogJpaEntity(
        id = this.id ?: 0L,
        userId = this.userId
    )
}

fun ConnectionLogJpaEntity.toDomain(): ConnectionLog {
    return ConnectionLog(
        id = this.id,
        userId = this.userId,
        createdAt = this.createdAt!!
    )
}
