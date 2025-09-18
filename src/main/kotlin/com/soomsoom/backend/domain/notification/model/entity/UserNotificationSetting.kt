package com.soomsoom.backend.domain.notification.model.entity

import java.time.LocalTime

/**
 * 사용자의 알림 수신 설정을 관리하는 Entity
 * @property id 고유 식별자
 * @property userId 설정을 소유한 사용자의 ID
 * @property diaryNotificationEnabled '마음일기' 작성 권유 알림 수신 여부
 * @property diaryNotificationTime '마음일기' 알림을 수신할 시간
 * @property soomsoomNewsNotificationEnabled '숨숨소식' (공지) 알림 수신 여부
 * @property reEngagementNotificationEnabled '숨숨인사' (미접속 재방문 유도) 알림 수신 여부
 */
class UserNotificationSetting(
    val id: Long = 0,
    val userId: Long,
    var diaryNotificationEnabled: Boolean,
    var diaryNotificationTime: LocalTime,
    var soomsoomNewsNotificationEnabled: Boolean,
    var reEngagementNotificationEnabled: Boolean,
) {
    /** '마음일기' 알림 시간을 업데이트 */
    fun updateDiaryTime(time: LocalTime) {
        this.diaryNotificationTime = time
    }

    /** '마음일기' 알림 수신 여부를 업데이트 */
    fun toggleDiary(enabled: Boolean) {
        this.diaryNotificationEnabled = enabled
    }

    /** '숨숨소식' 알림 수신 여부를 업데이트 */
    fun toggleNews(enabled: Boolean) {
        this.soomsoomNewsNotificationEnabled = enabled
    }

    /** '재방문 유도' 알림 수신 여부를 업데이트 */
    fun toggleReEngagement(enabled: Boolean) {
        this.reEngagementNotificationEnabled = enabled
    }
}
