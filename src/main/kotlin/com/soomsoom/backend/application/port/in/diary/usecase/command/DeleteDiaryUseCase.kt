package com.soomsoom.backend.application.port.`in`.diary.usecase.command

import com.soomsoom.backend.application.port.`in`.diary.command.DeleteDiaryCommand

interface DeleteDiaryUseCase {
    fun softDelete(command: DeleteDiaryCommand)
    fun hardDelete(command: DeleteDiaryCommand)
    fun deleteByUserId(userId: Long)
}
