package com.soomsoom.backend.adapter.out.persistence.user

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.UserOwnedCollectionQueryDslRepository
import com.soomsoom.backend.application.port.out.user.UserOwnedCollectionPort
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.stereotype.Component

@Component
class UserOwnedCollectionPersistenceAdapter(
    private val queryDslRepository: UserOwnedCollectionQueryDslRepository,
) : UserOwnedCollectionPort {

    override fun findCompletedCollectionIds(
        userId: Long,
        newItemId: Long,
        deletionStatus: DeletionStatus,
    ): List<Long> {
        return if (deletionStatus == DeletionStatus.ACTIVE) {
            queryDslRepository.findCompletedCollectionIds(userId, newItemId)
        } else {
            emptyList()
        }
    }
}
