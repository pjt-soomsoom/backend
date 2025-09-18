package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.notification.command.TrackNotificationClickCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.TrackNotificationClickUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TrackNotificationClickService(
    private val userNotificationPort: UserNotificationPort,
) : TrackNotificationClickUseCase {
    override fun command(command: TrackNotificationClickCommand) {
        val history = userNotificationPort.findHistoryById(command.historyId)
            ?: throw SoomSoomException(NotificationErrorCode.HISTORY_NOT_FOUND)

        val principal = SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
        if (history.userId != principal.id) {
            throw SoomSoomException(UserErrorCode.ACCESS_DENIED)
        }

        history.recordClick()

        userNotificationPort.saveHistory(history)
    }
}
