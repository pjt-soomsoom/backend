package com.soomsoom.backend.application.port.`in`.diary.query

import com.soomsoom.backend.domain.common.DeletionStatus
import java.time.LocalDate

data class GetDailyDiaryRecordCriteria(
    val userId: Long,
    val from: LocalDate,
    val to: LocalDate,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
