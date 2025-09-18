package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.CreateNotificationSettingCommand

interface CreateNotificationSettingUseCase {
    fun create(command: CreateNotificationSettingCommand): Long
}
