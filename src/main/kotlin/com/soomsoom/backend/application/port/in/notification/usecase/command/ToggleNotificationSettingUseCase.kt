package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.ToggleNotificationSettingCommand

interface ToggleNotificationSettingUseCase {
    fun command(command: ToggleNotificationSettingCommand)
}
