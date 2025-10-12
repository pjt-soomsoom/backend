package com.soomsoom.backend.adapter.out.notification

import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FCMAdapter(
    private val firebaseMessaging: FirebaseMessaging,
    private val userNotificationPort: UserNotificationPort,
) : NotificationPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(message: NotificationMessage) {
        sendAll(listOf(message))
    }

    override fun sendAll(messages: List<NotificationMessage>) {
        if (messages.isEmpty()) return

        // 모든 메시지를 내용별로 그룹핑 (title, body, payload가 모두 같아야 같은 그룹)
        val messagesByContent = messages.groupBy { Triple(it.title, it.body, it.payload) }

        messagesByContent.forEach { (_, messageGroup) ->
            val firstMessage = messageGroup.first()
            val userIds = messageGroup.map { it.targetUserId }
            val devices = userNotificationPort.findDevicesByUserIds(userIds)

            if (devices.isEmpty()) {
                log.warn("알림 보낼 대상 기기가 없습니다. userIds: {}", userIds)
                return@forEach
            }

            // 그룹핑된 메시지가 여러 명에게 가는 동일한 내용이라면, multicast로 최적화하여 발송
            if (devices.size > 1) {
                sendMulticast(firstMessage, devices)
            }
            // 그룹핑된 메시지가 단 한 건이거나, 개인화된 메시지라면 sendEach로 발송
            else if (devices.isNotEmpty()) {
                sendEach(devices, firstMessage)
            }
        }
    }

    private fun sendMulticast(domainMessage: NotificationMessage, devices: List<UserDevice>) {
        // iOS와 Android 기기를 분리 (ApnsConfig 때문에)
        val (iosDevices, androidDevices) = devices.partition { it.osType == OSType.IOS }

        if (iosDevices.isNotEmpty()) {
            val multicastMessage = buildFcmMulticastMessage(domainMessage, iosDevices, true)
            executeSend(multicastMessage, iosDevices.map { it.fcmToken })
        }
        if (androidDevices.isNotEmpty()) {
            val multicastMessage = buildFcmMulticastMessage(domainMessage, androidDevices, false)
            executeSend(multicastMessage, androidDevices.map { it.fcmToken })
        }
    }

    private fun sendEach(devices: List<UserDevice>, domainMessage: NotificationMessage) {
        if (devices.isEmpty()) return

        val fcmMessages = devices.map { buildFcmMessage(it, domainMessage) }

        val tokens = devices.map { it.fcmToken }

        executeSend(fcmMessages, tokens)
    }

    // 실제 발송 로직 공통화
    private fun executeSend(messages: Any, tokens: List<String>) {
        try {
            val response: BatchResponse = when (messages) {
                is MulticastMessage -> firebaseMessaging.sendEachForMulticast(messages)
                is List<*> -> firebaseMessaging.sendEach(messages as List<Message>)
                else -> return
            }

            if (response.failureCount > 0) {
                val failedTokens = mutableListOf<String>()
                response.responses.forEachIndexed { index, sendResponse ->
                    if (!sendResponse.isSuccessful) {
                        val failedToken = tokens[index]
                        val exception = sendResponse.exception
                        log.warn("FCM 발송 실패 - Token: [{}], Error: [{}]", failedToken, exception.message)

                        if (isTokenExpiredOrUnregistered(exception)) {
                            failedTokens.add(failedToken)
                        }
                    }
                }
                if (failedTokens.isNotEmpty()) {
                    userNotificationPort.deleteAllByToken(failedTokens)
                }
            }
            log.info("FCM 발송 성공: 총 {}건", response.successCount)
        } catch (e: FirebaseMessagingException) {
            log.error("FCM 발송 중 오류 발생", e)
        }
    }

    private fun buildFcmMessage(device: UserDevice, domainMessage: NotificationMessage): Message {
        val messageBuilder = Message.builder()
            .setToken(device.fcmToken)
            .putAllData(domainMessage.payload) // 데이터 페이로드는 항상 포함

        // title과 body가 있을 때만 OS 알림을 생성
        // '보이는 푸시'와 '데이터 전용 푸시'를 구분
        if (domainMessage.title != null && domainMessage.body != null) {
            val notification = Notification.builder()
                .setTitle(domainMessage.title)
                .setBody(domainMessage.body)
                .build()
            messageBuilder.setNotification(notification)
        }

        if (device.osType == OSType.IOS) {
            messageBuilder.setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setBadge(domainMessage.badgeCount) // iOS의 뱃지 카운트는 여기에 설정
                            .setSound("cat-meow-short-push.wav")
                            .build()
                    ).build()
            )
        }
        return messageBuilder.build()
    }
    private fun isTokenExpiredOrUnregistered(e: FirebaseMessagingException): Boolean {
        val errorCode = e.messagingErrorCode ?: return false
        return errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT
    }

    // MulticastMessage 빌더 추가
    private fun buildFcmMulticastMessage(domainMessage: NotificationMessage, devices: List<UserDevice>, isIos: Boolean): MulticastMessage {
        val messageBuilder = MulticastMessage.builder()
            .addAllTokens(devices.map { it.fcmToken })
            .putAllData(domainMessage.payload)
            .setNotification(Notification.builder().setTitle(domainMessage.title).setBody(domainMessage.body).build())

        if (isIos) {
            messageBuilder.setApnsConfig(
                ApnsConfig.builder()
                    .setAps(Aps.builder().setBadge(domainMessage.badgeCount).setSound("cat-meow-short-push.wav").build())
                    .build()
            )
        }
        return messageBuilder.build()
    }
}
