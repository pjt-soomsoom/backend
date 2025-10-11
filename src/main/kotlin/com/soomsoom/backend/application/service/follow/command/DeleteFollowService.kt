package com.soomsoom.backend.application.service.follow.command

import com.soomsoom.backend.application.port.`in`.follow.usecase.command.DeleteFollowUseCase
import com.soomsoom.backend.application.port.out.follow.FollowPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteFollowService(
    private val followPort: FollowPort,
) : DeleteFollowUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        followPort.deleteByUserId(userId)
    }
}
