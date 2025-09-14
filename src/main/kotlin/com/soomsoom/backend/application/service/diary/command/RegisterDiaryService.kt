package com.soomsoom.backend.application.service.diary.command

import com.soomsoom.backend.application.port.`in`.diary.command.RegisterDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.RegisterDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.RegisterDiaryUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import com.soomsoom.backend.domain.diary.model.Diary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class RegisterDiaryService(
    private val diaryPort: DiaryPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val dateHelper: DateHelper,
) : RegisterDiaryUseCase {
    override fun register(command: RegisterDiaryCommand): RegisterDiaryResult {
        val businessDay = dateHelper.getBusinessDay(LocalDateTime.now())
        if (diaryPort.existsByUserIdAndCreatedAtBetween(command.userId, businessDay.start, businessDay.end)) {
            throw SoomSoomException(DiaryErrorCode.DIARY_ALREADY_EXISTS)
        }

        val diary = Diary(
            userId = command.userId,
            emotion = command.emotion,
            memo = command.memo
        )
        val savedDiary = diaryPort.save(diary)

        val event = Event(
            eventType = EventType.DIARY_CREATED,
            payload = DiaryCreatedPayload(
                userId = savedDiary.userId,
                diaryId = savedDiary.id!!,
                emotion = savedDiary.emotion,
                createdAt = savedDiary.createdAt!!
            )
        )
        eventPublisher.publishEvent(event)

        return RegisterDiaryResult.from(savedDiary, dateHelper.getBusinessDate(savedDiary.createdAt!!))
    }
}
