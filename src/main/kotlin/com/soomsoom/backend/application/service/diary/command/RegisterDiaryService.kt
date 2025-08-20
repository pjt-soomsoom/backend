package com.soomsoom.backend.application.service.diary.command

import com.soomsoom.backend.application.port.`in`.diary.command.RegisterDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.RegisterDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.RegisterDiaryUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterDiaryService(
    private val diaryPort: DiaryPort,
) : RegisterDiaryUseCase {
    override fun register(command: RegisterDiaryCommand): RegisterDiaryResult {
        if (diaryPort.existsByUserIdAndRecordDate(command.userId, command.date)) {
            // 2. 이미 일기가 존재하면, 직접 정의한 예외를 발생시킵니다.
            throw SoomSoomException(DiaryErrorCode.DIARY_ALREADY_EXISTS)
        }

        val diary = Diary(
            userId = command.userId,
            emotion = command.emotion,
            memo = command.memo,
            recordDate = command.date
        )
        val savedDiary = diaryPort.save(diary)
        return RegisterDiaryResult.from(savedDiary)
    }
}
