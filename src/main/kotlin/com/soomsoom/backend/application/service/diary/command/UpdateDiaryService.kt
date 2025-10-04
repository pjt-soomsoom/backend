package com.soomsoom.backend.application.service.diary.command

import com.soomsoom.backend.application.port.`in`.diary.command.UpdateDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.UpdateDiaryUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateDiaryService(
    private val diaryPort: DiaryPort,
    private val dateHelper: DateHelper,
) : UpdateDiaryUseCase {

    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == authentication.principal.id")
    override fun update(command: UpdateDiaryCommand): FindDiaryResult {
        val diary = diaryPort.findById(command.diaryId)
            ?. also {
                it.update(
                    emotion = command.emotion,
                    memo = command.memo
                )
            }
            ?: throw SoomSoomException(DiaryErrorCode.NOT_FOUND)

        val savedDiary = diaryPort.save(diary)
        return FindDiaryResult.from(savedDiary, dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(savedDiary.createdAt!!)))
    }
}
