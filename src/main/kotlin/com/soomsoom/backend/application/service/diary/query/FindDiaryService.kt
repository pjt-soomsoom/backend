package com.soomsoom.backend.application.service.diary.query

import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.FindDiaryByIdUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindDiaryService(
    private val diaryPort: DiaryPort,
    private val dateHelper: DateHelper,
) : FindDiaryByIdUseCase {

    @PostAuthorize("hasRole('ADMIN') or (returnObject.userId == authentication.principal.id and #deletionStatus.name() == 'ACTIVE')")
    override fun findById(diaryId: Long, deletionStatus: DeletionStatus): FindDiaryResult {
        val diary = diaryPort.findById(diaryId, deletionStatus)
            ?: throw SoomSoomException(DiaryErrorCode.NOT_FOUND)
        return FindDiaryResult.from(diary, dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(diary.createdAt!!)))
    }
}
