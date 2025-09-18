package com.soomsoom.backend.application.port.out.notification

import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage

interface NotificationPort {
    /**
     * 단일 사용자에게 알림 메시지를 보냄
     */
    fun send(message: NotificationMessage)

    /**
     * 여러 사용자에게 동일한 내용의 알림 메시지를 한 번에 보냄
     */
    fun sendAll(messages: List<NotificationMessage>)
}
