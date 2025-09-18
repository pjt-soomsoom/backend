package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.application.port.`in`.notification.command.RegisterUserDeviceCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.RegisterUserDeviceUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterUserDeviceService(
    private val userNotificationPort: UserNotificationPort,
) : RegisterUserDeviceUseCase {

    /**
     * 사용자의 디바이스 정보를 등록하거나 갱신
     */
    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun command(command: RegisterUserDeviceCommand) {
        val existingDevice = userNotificationPort.findDeviceByToken(command.fcmToken)

        if (existingDevice == null) {
            // 2토큰이 존재하지 않으면, 새로운 기기로 판단하고 저장
            val newUserDevice = UserDevice(
                userId = command.userId,
                fcmToken = command.fcmToken,
                osType = command.osType
            )
            userNotificationPort.saveDevice(newUserDevice)
        } else {
            // 토큰이 이미 존재하면, 소유자를 현재 사용자로 갱신(update)
            // 이는 다른 계정으로 로그인했거나, 중고 기기인 경우를 처리
            if (existingDevice.userId != command.userId) {
                existingDevice.changeOwner(command.userId)
                userNotificationPort.saveDevice(existingDevice)
            }
        }
    }
}
