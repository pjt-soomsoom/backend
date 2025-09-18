package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.application.port.`in`.notification.command.ToggleNotificationSettingCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.ToggleNotificationSettingUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ToggleNotificationSettingsService(
    private val userNotificationPort: UserNotificationPort,
) : ToggleNotificationSettingUseCase {
    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun command(command: ToggleNotificationSettingCommand) {
        val settings = userNotificationPort.findSettingsByUserId(command.userId)
            ?: throw SoomSoomException(NotificationErrorCode.DEVICE_NOT_FOUND)

        when (command.type) {
            NotificationType.DIARY_REMINDER -> settings.toggleDiary(command.enabled)
            NotificationType.NEWS_UPDATE -> settings.toggleNews(command.enabled)
            NotificationType.RE_ENGAGEMENT -> settings.toggleReEngagement(command.enabled)
            else -> throw IllegalArgumentException("${command.type.name}은 토글할 수 없는 알림 타입입니다.")
        }

        userNotificationPort.saveSettings(settings)
    }
}
