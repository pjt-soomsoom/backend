package com.soomsoom.backend.adapter.`in`.web.api.diary.request

import com.soomsoom.backend.application.port.`in`.diary.query.GetDailyDiaryRecordCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class GetDailyDiaryRecordRequest(
    val userId: Long?,
    @field:NotNull(message = "조회 시작일은 필수입니다.")
    val from: LocalDate?,
    @field:NotNull(message = "조회 종료일은 필수입니다.")
    val to: LocalDate?,
    val deletionStatus: DeletionStatus?,
)

fun GetDailyDiaryRecordRequest.toCriteria(principalId: Long): GetDailyDiaryRecordCriteria {
    return GetDailyDiaryRecordCriteria(
        userId = this.userId ?: principalId, // 요청 받은 userId가 없으면 로그인한 사용자 ID 사용
        from = this.from!!,
        to = this.to!!,
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
