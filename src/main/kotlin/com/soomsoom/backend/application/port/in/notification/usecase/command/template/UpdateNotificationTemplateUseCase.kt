package com.soomsoom.backend.application.port.`in`.notification.usecase.command.template

import com.soomsoom.backend.application.port.`in`.notification.command.template.UpdateNotificationTemplateCommand

interface UpdateNotificationTemplateUseCase {
    fun command(command: UpdateNotificationTemplateCommand)
}
