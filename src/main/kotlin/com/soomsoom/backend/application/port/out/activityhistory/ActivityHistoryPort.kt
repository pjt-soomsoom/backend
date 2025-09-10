package com.soomsoom.backend.application.port.out.activityhistory

import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary
import java.time.LocalDate

interface ActivityHistoryPort {
    // ActivityProgress (이어듣기) 관련
    fun findProgress(userId: Long, activityId: Long): ActivityProgress?
    fun saveProgress(progress: ActivityProgress): ActivityProgress

    // ActivityCompletionLog (완료 기록) 관련
    fun saveCompletionLog(log: ActivityCompletionLog): ActivityCompletionLog

    // UserActivitySummary (누적 통계) 관련
    fun findUserSummary(userId: Long): UserActivitySummary?
    fun saveUserSummary(summary: UserActivitySummary): UserActivitySummary

    // 마이페이지 요약 정보 조회를 위한 카운트 메서드
    fun countCompletedActivities(userId: Long): Long

    /**
     * 특정 날짜를 제외하고, 해당 날짜 이전에 가장 최근의 활동 완료 기록을 조회 (연속 달성 체크용)
     */
    fun findLatestCompletionLog(userId: Long, activityType: ActivityType, targetDate: LocalDate): ActivityCompletionLog?

    /**
     * 특정 월의 활동 완료 횟수를 조회 (월간 달성 체크용)
     */
    fun countMonthlyCompletion(userId: Long, activityType: ActivityType, from: LocalDate, to: LocalDate): Long

    /**
     * 완료한 활동의 종류(개수)를 조회 (다양한 종류 활동 완료 체크용)
     */
    fun countDistinctActivity(userId: Long, activityType: ActivityType): Long
}
