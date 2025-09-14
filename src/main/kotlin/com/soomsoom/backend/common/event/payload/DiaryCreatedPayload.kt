package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.diary.model.Emotion
import java.time.LocalDateTime

data class DiaryCreatedPayload(
    val userId: Long,
    val diaryId: Long,
    val emotion: Emotion,
    val createdAt: LocalDateTime,
) : Payload
