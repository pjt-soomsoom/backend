package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "user_notification_settings")
class UserNotificationSettingJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val userId: Long,
    var diaryNotificationEnabled: Boolean,
    var diaryNotificationTime: LocalTime,
    var soomsoomNewsNotificationEnabled: Boolean,
    var reEngagementNotificationEnabled: Boolean,
) : BaseTimeEntity() {
    fun update(domain: UserNotificationSetting) {
        this.diaryNotificationEnabled = domain.diaryNotificationEnabled
        this.diaryNotificationTime = domain.diaryNotificationTime
        this.soomsoomNewsNotificationEnabled = domain.soomsoomNewsNotificationEnabled
        this.reEngagementNotificationEnabled = domain.reEngagementNotificationEnabled
    }
}
