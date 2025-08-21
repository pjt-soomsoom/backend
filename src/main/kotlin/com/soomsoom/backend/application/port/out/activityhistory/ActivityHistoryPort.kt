package com.soomsoom.backend.application.port.out.activityhistory

import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary

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
}
