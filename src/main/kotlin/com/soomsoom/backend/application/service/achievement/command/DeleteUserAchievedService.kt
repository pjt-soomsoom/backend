package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteUserAchievedUseCase
import com.soomsoom.backend.application.port.out.achievement.UserAchievedPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserAchievedService(
    private val userAchievedPort: UserAchievedPort,
) : DeleteUserAchievedUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        userAchievedPort.deleteUserAchievedByUserId(userId)
    }
}
