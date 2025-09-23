package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(
    name = "user_notification_settings",
    indexes = [
        Index(name = "idx_uns_diary_reminder", columnList = "diary_notification_enabled, diary_notification_time")
    ]
)
class UserNotificationSettingJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val userId: Long,

    @Column(name = "diary_notification_enabled")
    var diaryNotificationEnabled: Boolean,

    @Column(name = "diary_notification_time")
    var diaryNotificationTime: LocalTime,

    @Column(name = "soomsoom_news_notification_enabled")
    var soomsoomNewsNotificationEnabled: Boolean,

    @Column(name = "re_engagement_notification_enabled")
    var reEngagementNotificationEnabled: Boolean,
) : BaseTimeEntity() {
    fun update(domain: UserNotificationSetting) {
        this.diaryNotificationEnabled = domain.diaryNotificationEnabled
        this.diaryNotificationTime = domain.diaryNotificationTime
        this.soomsoomNewsNotificationEnabled = domain.soomsoomNewsNotificationEnabled
        this.reEngagementNotificationEnabled = domain.reEngagementNotificationEnabled
    }
}
