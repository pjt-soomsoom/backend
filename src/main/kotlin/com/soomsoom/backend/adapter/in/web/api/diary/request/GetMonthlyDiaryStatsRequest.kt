package com.soomsoom.backend.adapter.`in`.web.api.diary.request

import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.constraints.NotNull
import java.time.YearMonth

data class GetMonthlyDiaryStatsRequest(
    val userId: Long?,
    @field:NotNull(message = "조회 년도(year)는 필수입니다.")
    val year: Int?,
    @field:NotNull(message = "조회 월(month)은 필수입니다.")
    val month: Int?,
    val deletionStatus: DeletionStatus?,
)

fun GetMonthlyDiaryStatsRequest.toCriteria(principalId: Long): GetMonthlyDiaryStatsCriteria {
    return GetMonthlyDiaryStatsCriteria(
        userId = this.userId ?: principalId,
        yearMonth = YearMonth.of(this.year!!, this.month!!),
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
