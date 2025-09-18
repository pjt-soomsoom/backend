package com.soomsoom.backend.adapter.out.persistence.notification

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.MessageVariationJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationHistoryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationTemplateJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserNotificationSettingJpaEntity
import com.soomsoom.backend.domain.notification.model.entity.MessageVariation
import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.entity.NotificationTemplate
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting

fun NotificationTemplateJpaEntity.toDomain(): NotificationTemplate {
    val domainVariations = mutableListOf<MessageVariation>()

    val template = NotificationTemplate(
        id = this.id,
        type = this.type,
        description = this.description,
        isActive = this.isActive,
        triggerCondition = this.triggerCondition,
        variations = domainVariations // 참조 전달
    )

    this.variations.forEach { jpaVariation ->
        domainVariations.add(jpaVariation.toDomain(template))
    }

    return template
}

fun MessageVariationJpaEntity.toDomain(template: NotificationTemplate): MessageVariation {
    return MessageVariation(
        id = this.id,
        notificationTemplate = template, // 부모 참조 설정
        titleTemplate = this.titleTemplate,
        bodyTemplate = this.bodyTemplate,
        isActive = this.isActive
    )
}

fun NotificationTemplate.toEntity(): NotificationTemplateJpaEntity {
    val templateEntity = NotificationTemplateJpaEntity(
        id = this.id,
        type = this.type,
        description = this.description,
        isActive = this.isActive,
        triggerCondition = this.triggerCondition
    )

    // 도메인 자식 객체들을 순회하며 엔티티로 변환하고, 부모-자식 관계를 맺어줍니다.
    this.variations.forEach { domainVariation ->
        val variationEntity = domainVariation.toEntity(templateEntity)
        templateEntity.variations.add(variationEntity)
    }
    return templateEntity
}

fun MessageVariation.toEntity(templateEntity: NotificationTemplateJpaEntity): MessageVariationJpaEntity {
    return MessageVariationJpaEntity(
        id = this.id,
        notificationTemplate = templateEntity, // 부모 참조 설정
        titleTemplate = this.titleTemplate,
        bodyTemplate = this.bodyTemplate,
        isActive = this.isActive
    )
}
// UserNotificationSetting <-> UserNotificationSettingJpaEntity
fun UserNotificationSetting.toEntity(): UserNotificationSettingJpaEntity {
    return UserNotificationSettingJpaEntity(
        id = this.id,
        userId = this.userId,
        diaryNotificationEnabled = this.diaryNotificationEnabled,
        diaryNotificationTime = this.diaryNotificationTime,
        soomsoomNewsNotificationEnabled = this.soomsoomNewsNotificationEnabled,
        reEngagementNotificationEnabled = this.reEngagementNotificationEnabled
    )
}

fun UserNotificationSettingJpaEntity.toDomain(): UserNotificationSetting {
    return UserNotificationSetting(
        id = this.id,
        userId = this.userId,
        diaryNotificationEnabled = this.diaryNotificationEnabled,
        diaryNotificationTime = this.diaryNotificationTime,
        soomsoomNewsNotificationEnabled = this.soomsoomNewsNotificationEnabled,
        reEngagementNotificationEnabled = this.reEngagementNotificationEnabled
    )
}

// UserDevice <-> UserDeviceJpaEntity
fun UserDevice.toEntity(): UserDeviceJpaEntity {
    return UserDeviceJpaEntity(
        id = this.id,
        userId = this.userId,
        fcmToken = this.fcmToken,
        osType = this.osType
    )
}

fun UserDeviceJpaEntity.toDomain(): UserDevice {
    return UserDevice(
        id = this.id,
        userId = this.userId,
        fcmToken = this.fcmToken,
        osType = this.osType
    )
}

// NotificationHistory <-> NotificationHistoryJpaEntity
fun NotificationHistory.toEntity(): NotificationHistoryJpaEntity {
    return NotificationHistoryJpaEntity(
        id = this.id,
        userId = this.userId,
        messageVariationId = this.messageVariationId,
        sentAt = this.sentAt,
        clickedAt = this.clickedAt
    )
}

fun NotificationHistoryJpaEntity.toDomain(): NotificationHistory {
    return NotificationHistory(
        id = this.id,
        userId = this.userId,
        messageVariationId = this.messageVariationId,
        sentAt = this.sentAt,
        clickedAt = this.clickedAt
    )
}
