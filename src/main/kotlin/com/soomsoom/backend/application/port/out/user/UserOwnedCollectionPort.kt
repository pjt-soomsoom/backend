package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.common.DeletionStatus

interface UserOwnedCollectionPort {
    fun findCompletedCollectionIds(
        userId: Long,
        newItemId: Long,
        deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    ): List<Long>
}
