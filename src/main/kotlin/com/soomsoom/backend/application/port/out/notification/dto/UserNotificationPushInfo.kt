package com.soomsoom.backend.application.port.out.notification.dto

data class UserNotificationPushInfo(
    val userId: Long,
    val isNewsNotificationEnabled: Boolean,
    val unreadAnnouncementCount: Int,
)
