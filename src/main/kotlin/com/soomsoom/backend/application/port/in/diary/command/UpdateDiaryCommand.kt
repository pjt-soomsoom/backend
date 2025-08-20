package com.soomsoom.backend.application.port.`in`.diary.command

import com.soomsoom.backend.domain.diary.model.Emotion

data class UpdateDiaryCommand(
    val diaryId: Long,
    val emotion: Emotion,
    val memo: String?,
)
