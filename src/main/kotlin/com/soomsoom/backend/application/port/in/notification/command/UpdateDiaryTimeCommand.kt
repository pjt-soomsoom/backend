package com.soomsoom.backend.application.port.`in`.notification.command

import java.time.LocalTime

data class UpdateDiaryTimeCommand(
    val userId: Long,
    val diaryNotificationTime: LocalTime,
)
