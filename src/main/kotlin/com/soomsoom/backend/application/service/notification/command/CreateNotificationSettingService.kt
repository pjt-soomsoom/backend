package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.application.port.`in`.notification.command.CreateNotificationSettingCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.CreateNotificationSettingUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@Service
@Transactional
class CreateNotificationSettingService(
    private val userNotificationPort: UserNotificationPort,
    @Value("\${diary.default-time}")
    val diaryDefaultTime: LocalTime,
) : CreateNotificationSettingUseCase {

    override fun create(command: CreateNotificationSettingCommand): Long {
        val userNotificationSetting = UserNotificationSetting(
            userId = command.userId,
            diaryNotificationEnabled = command.diaryNotificationEnabled,
            soomsoomNewsNotificationEnabled = command.soomsoomNewsNotificationEnabled,
            reEngagementNotificationEnabled = command.reEngagementNotificationEnabled,
            diaryNotificationTime = diaryDefaultTime
        )
        return userNotificationPort.saveSettings(userNotificationSetting).id
    }
}
