package com.soomsoom.backend.application.service.todaymission.query

import com.soomsoom.backend.application.port.`in`.todaymission.dto.TodayMissionResult
import com.soomsoom.backend.application.port.`in`.todaymission.usecase.query.FindTodayMissionUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.todaymission.model.enums.MissionStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class FindTodayMissionService(
    private val diaryPort: DiaryPort,
    private val activityHistoryPort: ActivityHistoryPort,
    private val dateHelper: DateHelper,
) : FindTodayMissionUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun find(userId: Long): TodayMissionResult {
        // "오늘"의 비즈니스 하루 정보를 가져오기
        val today = dateHelper.getBusinessDay(LocalDateTime.now())

        // 오늘 쓴 일기가 있는지 확인
        val hasWrittenDiaryToday = diaryPort.existsByUserIdAndCreatedAtBetween(userId, today.start, today.end)

        // 만약 일기를 쓰지 않았다면, 즉시 NEED_DIARY를 반환
        if (!hasWrittenDiaryToday) {
            return TodayMissionResult(MissionStatus.NEED_DIARY)
        }

        // 일기를 썼을 경우에만 활동 완료 여부를 확인
        val hasCompletedActivityToday = activityHistoryPort.existsByUserIdAndTypesAndCreatedAtBetween(
            userId = userId,
            activityTypes = listOf(ActivityType.MEDITATION, ActivityType.MEDITATION),
            from = today.start,
            to = today.end
        )

        val status = if (hasCompletedActivityToday) {
            MissionStatus.ALL_DONE
        } else {
            MissionStatus.NEED_ACTIVITY
        }

        return TodayMissionResult(status)
    }
}
