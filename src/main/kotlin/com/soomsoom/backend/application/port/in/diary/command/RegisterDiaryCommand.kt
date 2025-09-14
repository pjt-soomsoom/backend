package com.soomsoom.backend.application.port.`in`.diary.command

import com.soomsoom.backend.domain.diary.model.Emotion

data class RegisterDiaryCommand(
    val userId: Long,
    val emotion: Emotion,
    val memo: String?,
)
