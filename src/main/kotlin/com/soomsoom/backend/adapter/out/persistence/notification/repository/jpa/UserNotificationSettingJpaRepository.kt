package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserNotificationSettingJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalTime

interface UserNotificationSettingJpaRepository : JpaRepository<UserNotificationSettingJpaEntity, Long> {
    fun findByUserId(userId: Long): UserNotificationSettingJpaEntity?

    // 페이징된 결과를 반환하기 위한 JPQL 쿼리
    @Query(
        "SELECT uns.userId FROM UserNotificationSettingJpaEntity uns WHERE uns.diaryNotificationEnabled = true AND uns.diaryNotificationTime = :time"
    )
    fun findUserIdsByDiaryNotificationTime(time: LocalTime, pageable: Pageable): List<Long>
}
