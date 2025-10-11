package com.soomsoom.backend.application.port.out.notification

import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.application.port.out.notification.dto.UserNotificationPushInfo
import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.entity.UserDevice
import com.soomsoom.backend.domain.notification.model.entity.UserNotificationSetting
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.time.LocalTime

interface UserNotificationPort {
    // UserDevice
    fun findDeviceByToken(fcmToken: String): UserDevice?
    fun saveDevice(device: UserDevice): UserDevice
    fun deleteDeviceByToken(fcmToken: String)
    fun deleteAllByToken(fcmToken: List<String>)
    fun findDevicesByUserIds(userIds: List<Long>): List<UserDevice>
    fun deleteUserDeviceByUserId(userId: Long)

    // UserNotificationSetting
    fun findSettingsByUserId(userId: Long): UserNotificationSetting?
    fun saveSettings(settings: UserNotificationSetting): UserNotificationSetting
    fun deleteUserNotificationSettingByUserId(userId: Long)

    // NotificationHistory
    fun saveHistory(history: NotificationHistory): NotificationHistory
    fun findHistoryById(id: Long): NotificationHistory?
    fun deleteNotificationHistoryByUserId(userId: Long)

    /**
     * '마음일기 알림(인게이지먼트 넛지)' 발송 대상을 조회
     * - 어제는 접속했지만, 오늘 일기나 활동을 하지 않은 사용자
     * @param yesterdayStart 어제 날짜 시작 시간
     * @param yesterdayEnd 어제 날짜 종료 시간
     * @param todayStart 오늘 날짜 시작 시간
     * @param todayEnd 오늘 날짜 종료 시간
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return 대상 사용자 ID 리스트
     */
    fun findDiaryReminderTargetUserIds(
        targetTime: LocalTime,
        yesterdayStart: LocalDateTime,
        yesterdayEnd: LocalDateTime,
        todayStart: LocalDateTime,
        todayEnd: LocalDateTime,
        pageNumber: Int,
        pageSize: Int,
    ): List<Long>

    /**
     * '시간 범위 조건 맵'을 기반으로, 조건에 맞는 미접속 사용자 목록과
     * 각자의 미접속 일수를 페이징하여 조회
     *
     * @param inactivityConditions Key: 미접속 일수, Value: 해당 미접속 일수에 해당하는 마지막 접속 시간 범위(from, to)
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return InactiveUserAdapterDto 목록 (userId, inactiveDays)
     */
    fun findReEngagementTargets(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto>

    /**
     * 공지 사항 푸시 알림 발송에 필요한 사용자 정보를 페이지네이션하여 한 번에 조회
     */
    fun findUserNotificationPushInfos(pageable: Pageable): List<UserNotificationPushInfo>
}
