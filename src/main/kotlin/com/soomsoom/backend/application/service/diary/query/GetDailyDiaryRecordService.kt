package com.soomsoom.backend.application.service.diary.query

import com.soomsoom.backend.application.port.`in`.diary.dto.GetDailyDiaryRecordResult
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.GetDailyDiaryRecordUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.utils.DateHelper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetDailyDiaryRecordService(
    private val diaryPort: DiaryPort,
    private val dateHelper: DateHelper,
) : GetDailyDiaryRecordUseCase {

    /**
     *  마음 리포트 하단의 요일별 감정 기록
     */
    @PreAuthorize("hasRole('ADMIN') or (#criteria.userId == authentication.principal.userId and #criteria.deletionStatus.name() == 'ACTIVE')")
    override fun getDailyDiaryRecord(criteria: GetDailyDiaryRecordCriteria): List<GetDailyDiaryRecordResult> {
        val businessPeriod = dateHelper.getBusinessPeriod(criteria.from, criteria.to)
        val dailyRecords = diaryPort.getDailyDiaryRecords(
            criteria.userId,
            businessPeriod.start,
            businessPeriod.end,
            criteria.deletionStatus
        )
        return dailyRecords.map { GetDailyDiaryRecordResult.from(it, dateHelper.getBusinessDate(it.createdAt)) }
    }
}
