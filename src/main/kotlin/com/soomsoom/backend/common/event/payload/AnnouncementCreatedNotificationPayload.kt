package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload

class AnnouncementCreatedNotificationPayload(
    val announcementId: Long,
    val title: String,
) : NotificationPayload
