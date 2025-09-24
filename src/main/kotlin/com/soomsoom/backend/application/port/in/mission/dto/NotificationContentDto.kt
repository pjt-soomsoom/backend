package com.soomsoom.backend.application.port.`in`.mission.dto

import com.soomsoom.backend.domain.mission.model.vo.NotificationContent

/**
 * 알림 메시지의 제목과 본문을 나타내는 DTO입니다.
 */
data class NotificationContentDto(
    val title: String,
    val body: String,
) {
    companion object {
        fun from(content: NotificationContent): NotificationContentDto {
            return NotificationContentDto(
                title = content.title,
                body = content.body
            )
        }
    }
}
