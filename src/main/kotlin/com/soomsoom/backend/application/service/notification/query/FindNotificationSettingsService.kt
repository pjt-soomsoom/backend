package com.soomsoom.backend.application.service.notification.query

import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationSettingsDto
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.FindNotificationSettingsUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindNotificationSettingsService(
    private val userNotificationPort: UserNotificationPort,
) : FindNotificationSettingsUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun findByUserId(userId: Long): NotificationSettingsDto {
        val settings = userNotificationPort.findSettingsByUserId(userId)
            ?: throw SoomSoomException(NotificationErrorCode.DEVICE_NOT_FOUND)

        return NotificationSettingsDto(
            diaryNotificationEnabled = settings.diaryNotificationEnabled,
            diaryNotificationTime = settings.diaryNotificationTime,
            soomsoomNewsNotificationEnabled = settings.soomsoomNewsNotificationEnabled,
            reEngagementNotificationEnabled = settings.reEngagementNotificationEnabled
        )
    }
}
