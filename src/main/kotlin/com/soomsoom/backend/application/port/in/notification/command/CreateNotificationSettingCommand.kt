package com.soomsoom.backend.application.port.`in`.notification.command

data class CreateNotificationSettingCommand(
    var userId: Long,
    var diaryNotificationEnabled: Boolean,
    var soomsoomNewsNotificationEnabled: Boolean,
    var reEngagementNotificationEnabled: Boolean,
)
