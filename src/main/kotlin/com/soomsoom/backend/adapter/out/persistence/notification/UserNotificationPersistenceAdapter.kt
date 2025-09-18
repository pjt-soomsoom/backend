package com.soomsoom.backend.adapter.out.persistence.notification

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.NotificationHistoryJpaRepository
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.UserDeviceJpaRepository
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.UserNotificationQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.UserNotificationSettingJpaRepository
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class UserNotificationPersistenceAdapter(
    private val deviceJpaRepository: UserDeviceJpaRepository,
    private val userNotificationSettingJpaRepository: UserNotificationSettingJpaRepository,
    private val notificationHistoryJpaRepository: NotificationHistoryJpaRepository,
    private val userNotificationQueryDslRepository: UserNotificationQueryDslRepository,
) : UserNotificationPort {
    override fun findDeviceByToken(fcmToken: String): UserDevice? {
        return deviceJpaRepository.findByFcmToken(fcmToken)?.toDomain()
    }

    override fun saveDevice(device: UserDevice): UserDevice {
        return deviceJpaRepository.save(device.toEntity()).toDomain()
    }

    override fun deleteDeviceByToken(fcmToken: String) {
        deviceJpaRepository.deleteByFcmToken(fcmToken)
    }

    override fun deleteAllByToken(fcmToken: List<String>) {
        deviceJpaRepository.deleteAllByFcmTokenIn(fcmToken)
    }

    override fun findDevicesByUserIds(userIds: List<Long>): List<UserDevice> {
        return userNotificationQueryDslRepository.findDevicesByUserIds(userIds).map { it.toDomain() }
    }

    override fun findSettingsByUserId(userId: Long): UserNotificationSetting? {
        return userNotificationSettingJpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun saveSettings(settings: UserNotificationSetting): UserNotificationSetting {
        val entity = userNotificationSettingJpaRepository.findByUserId(settings.userId)?.also {
            it.update(settings)
        } ?: settings.toEntity()
        return userNotificationSettingJpaRepository.save(entity).toDomain()
    }

    override fun saveHistory(history: NotificationHistory): NotificationHistory {
        return notificationHistoryJpaRepository.save(history.toEntity()).toDomain()
    }

    override fun findHistoryById(id: Long): NotificationHistory? {
        return notificationHistoryJpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findDiaryReminderTargetUserIds(
        targetTime: LocalTime,
        yesterdayStart: LocalDateTime,
        yesterdayEnd: LocalDateTime,
        todayStart: LocalDateTime,
        todayEnd: LocalDateTime,
        pageNumber: Int,
        pageSize: Int,
    ): List<Long> {
        return userNotificationQueryDslRepository.findDiaryReminderTargetUserIds(
            targetTime = targetTime,
            yesterdayStart = yesterdayStart,
            yesterdayEnd = yesterdayEnd,
            todayStart = todayStart,
            todayEnd = todayEnd,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
    }

    override fun findReEngagementTargets(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto> {
        return userNotificationQueryDslRepository.findReEngagementTargets(
            inactivityConditions = inactivityConditions,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
    }
}
