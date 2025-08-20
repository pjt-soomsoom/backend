package com.soomsoom.backend.application.service.diary.query

import com.soomsoom.backend.application.port.`in`.diary.dto.GetDailyDiaryRecordResult
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.GetDailyDiaryRecordUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetDailyDiaryRecordService(
    private val diaryPort: DiaryPort,
) : GetDailyDiaryRecordUseCase {

    @PreAuthorize("hasRole('ADMIN') or (#criteria.userId == authentication.principal.userId and #criteria.deletionStatus.name() == 'ACTIVE')")
    override fun getDailyDiaryRecord(criteria: GetDailyDiaryRecordCriteria): List<GetDailyDiaryRecordResult> {
        val dailyRecords = diaryPort.getDailyDiaryRecords(criteria)
        return dailyRecords.map { GetDailyDiaryRecordResult.from(it) }
    }
}
