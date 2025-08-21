package com.soomsoom.backend.application.service.activityhistory.query

import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindMySummaryResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindMySummaryUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindMySummaryService(
    private val activityHistoryPort: ActivityHistoryPort,
    private val diaryPort: DiaryPort,
) : FindMySummaryUseCase {
    /**
     * 특정 사용자의 활동 요약 정보를 조회
     * @param userId 조회할 사용자의 ID
     * @return 일기 작성 횟수, 활동 완료 횟수, 총 활동 시간 정보
     */
    override fun find(userId: Long): FindMySummaryResult {
        // 각 Port를 통해 필요한 데이터를 각각 조회
        val summary = activityHistoryPort.findUserSummary(userId)
        val diaryCount = diaryPort.countByUserId(userId)
        val activityCount = activityHistoryPort.countCompletedActivities(userId)

        // 조회된 데이터들을 조합하여 최종 DTO를 만들어 반환
        return FindMySummaryResult(
            diaryCount = diaryCount,
            activityCount = activityCount,
            totalActivitySeconds = summary?.totalPlaySeconds ?: 0L
        )
    }
}
