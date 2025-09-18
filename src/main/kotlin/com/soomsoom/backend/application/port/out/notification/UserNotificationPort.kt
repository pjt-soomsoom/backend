package com.soomsoom.backend.application.port.out.notification

import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting

interface UserNotificationPort {
    // UserDevice
    fun findDeviceByToken(fcmToken: String): UserDevice?
    fun saveDevice(device: UserDevice): UserDevice
    fun deleteDeviceByToken(fcmToken: String)
    fun deleteAllByToken(fcmToken: List<String>)
    fun findDevicesByUserIds(userIds: List<Long>): List<UserDevice>

    // UserNotificationSetting
    fun findSettingsByUserId(userId: Long): UserNotificationSetting?
    fun saveSettings(settings: UserNotificationSetting): UserNotificationSetting

    // NotificationHistory
    fun saveHistory(history: NotificationHistory): NotificationHistory
    fun findHistoryById(id: Long): NotificationHistory?
}
