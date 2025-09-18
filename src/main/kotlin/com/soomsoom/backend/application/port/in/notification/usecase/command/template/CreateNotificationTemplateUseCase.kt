package com.soomsoom.backend.application.port.`in`.notification.usecase.command.template

import com.soomsoom.backend.application.port.`in`.notification.command.template.CreateNotificationTemplateCommand

interface CreateNotificationTemplateUseCase {
    fun command(command: CreateNotificationTemplateCommand): Long
}
