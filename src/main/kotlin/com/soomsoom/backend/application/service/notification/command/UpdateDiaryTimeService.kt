package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.application.port.`in`.notification.command.UpdateDiaryTimeCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.UpdateDiaryTimeUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateDiaryTimeService(
    private val userNotificationPort: UserNotificationPort,
) : UpdateDiaryTimeUseCase {
    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun command(command: UpdateDiaryTimeCommand) {
        val settings = userNotificationPort.findSettingsByUserId(command.userId)
            ?: throw SoomSoomException(NotificationErrorCode.DEVICE_NOT_FOUND)

        settings.updateDiaryTime(command.diaryNotificationTime)

        userNotificationPort.saveSettings(settings)
    }
}
