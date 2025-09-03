package com.soomsoom.backend.application.service.diary.command

import com.soomsoom.backend.application.port.`in`.diary.command.RegisterDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.RegisterDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.RegisterDiaryUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterDiaryService(
    private val diaryPort: DiaryPort,
    private val eventPublisher: ApplicationEventPublisher,
) : RegisterDiaryUseCase {
    override fun register(command: RegisterDiaryCommand): RegisterDiaryResult {
        if (diaryPort.existsByUserIdAndRecordDate(command.userId, command.date)) {
            throw SoomSoomException(DiaryErrorCode.DIARY_ALREADY_EXISTS)
        }

        val diary = Diary(
            userId = command.userId,
            emotion = command.emotion,
            memo = command.memo,
            recordDate = command.date
        )
        val savedDiary = diaryPort.save(diary)

        val event = Event(
            eventType = EventType.DIARY_CREATED,
            payload = DiaryCreatedPayload(
                userId = savedDiary.userId,
                diaryId = savedDiary.id!!,
                recordDate = diary.recordDate,
                emotion = diary.emotion
            )
        )
        eventPublisher.publishEvent(event)

        return RegisterDiaryResult.from(savedDiary)
    }
}
