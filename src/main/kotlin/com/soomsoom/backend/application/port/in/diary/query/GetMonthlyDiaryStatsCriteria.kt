package com.soomsoom.backend.application.port.`in`.diary.query

import com.soomsoom.backend.domain.common.DeletionStatus
import java.time.YearMonth

data class GetMonthlyDiaryStatsCriteria(
    val userId: Long,
    val yearMonth: YearMonth,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
