package com.soomsoom.backend.application.port.`in`.diary.usecase.command

import com.soomsoom.backend.application.port.`in`.diary.command.UpdateDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult

interface UpdateDiaryUseCase {
    fun update(command: UpdateDiaryCommand): FindDiaryResult
}
