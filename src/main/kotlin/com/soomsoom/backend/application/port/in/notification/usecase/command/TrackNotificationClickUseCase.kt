package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.TrackNotificationClickCommand

interface TrackNotificationClickUseCase {
    fun command(command: TrackNotificationClickCommand)
}
