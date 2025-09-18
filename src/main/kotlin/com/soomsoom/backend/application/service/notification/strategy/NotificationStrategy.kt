package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.NotificationPayload

/**
 * 특정 NotificationPayload를 기반으로 그에 맞는 알림 메시지를 생성하는 전략 인터페이스
 * @param P 이 전략이 처리할 수 있는, NotificationPayload를 구현한 특정 Payload 타입
 */
interface NotificationStrategy<P : NotificationPayload> {
    /**
     * 주어진 이벤트의 페이로드를 이 전략이 처리할 수 있는지 확인
     */
    fun supports(event: Event<*>): Boolean

    /**
     * 이벤트를 기반으로 발송할 알림 메시지 목록을 생성해아여 발송
     */
    fun execute(event: Event<P>)
}
