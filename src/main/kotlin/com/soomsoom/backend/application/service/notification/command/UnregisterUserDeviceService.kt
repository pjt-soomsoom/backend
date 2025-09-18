package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.notification.command.UnregisterUserDeviceCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.UnregisterUserDeviceUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnregisterUserDeviceService(
    private val userNotificationPort: UserNotificationPort,
) : UnregisterUserDeviceUseCase {

    /**
     * 사용자의 디바이스 정보를 삭제
     */
    override fun command(command: UnregisterUserDeviceCommand) {
        val device = userNotificationPort.findDeviceByToken(command.fcmToken)
        if (device != null) {
            val principal = SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
            if (device.userId == principal.id) {
                // 권한이 확인되면 삭제를 진행
                userNotificationPort.deleteDeviceByToken(command.fcmToken)
            } else {
                // 남의 디바이스를 삭제하려는 시도
                throw SoomSoomException(UserErrorCode.ACCESS_DENIED)
            }
        }
    }
}
