package com.soomsoom.backend.application.port.`in`.diary.command

data class DeleteDiaryCommand(
    val diaryId: Long,
    val principalId: Long,
)
