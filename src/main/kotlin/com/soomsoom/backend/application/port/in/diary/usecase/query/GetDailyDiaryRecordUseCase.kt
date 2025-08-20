package com.soomsoom.backend.application.port.`in`.diary.usecase.query

import com.soomsoom.backend.application.port.`in`.diary.dto.GetDailyDiaryRecordResult
import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria

interface GetDailyDiaryRecordUseCase {
    fun getDailyDiaryRecord(criteria: GetDailyDiaryRecordCriteria): List<GetDailyDiaryRecordResult>
}
