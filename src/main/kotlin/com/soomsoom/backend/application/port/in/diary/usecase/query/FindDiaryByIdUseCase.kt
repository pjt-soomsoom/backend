package com.soomsoom.backend.application.port.`in`.diary.usecase.query

import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.domain.common.DeletionStatus

interface FindDiaryByIdUseCase {
    fun findById(diaryId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): FindDiaryResult
}
