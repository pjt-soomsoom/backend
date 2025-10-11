package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteUserProgressUseCase
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserProgressService(
    private val userProgressPort: UserProgressPort,
) : DeleteUserProgressUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        userProgressPort.deleteUserProgressByUserId(userId)
    }
}
