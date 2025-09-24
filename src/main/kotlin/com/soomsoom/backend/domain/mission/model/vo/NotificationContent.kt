package com.soomsoom.backend.domain.mission.model.vo

/**
 * 푸시 알림 등 사용자에게 보낼 메시지의 제목과 본문을 나타내는 값 객
 *
 * @property title 알림의 제목입니다.
 * @property body 알림의 본문 내용입니다.
 */
data class NotificationContent(
    val title: String,
    val body: String,
)
