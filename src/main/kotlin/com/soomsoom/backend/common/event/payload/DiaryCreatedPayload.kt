package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDate

data class DiaryCreatedPayload(
    val userId: Long,
    val diaryId: Long,
    val recordDate: LocalDate,
    val emotion: Emotion,
) : Payload
