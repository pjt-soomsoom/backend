package com.soomsoom.backend.application.port.`in`.item.query

import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Pageable

data class FindCollectionsCriteria (
    val userId: Long,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val pageable: Pageable,
)
