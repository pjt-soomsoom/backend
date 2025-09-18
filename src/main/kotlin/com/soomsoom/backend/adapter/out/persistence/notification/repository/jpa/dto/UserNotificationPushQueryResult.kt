package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection

data class UserNotificationPushQueryResult @QueryProjection constructor(
    val userId: Long,
    val isNewsNotificationEnabled: Boolean,
    val unreadAnnouncementCount: Int,
)
