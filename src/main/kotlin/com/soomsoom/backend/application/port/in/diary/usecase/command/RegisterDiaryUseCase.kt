package com.soomsoom.backend.application.port.`in`.diary.usecase.command

import com.soomsoom.backend.application.port.`in`.diary.command.RegisterDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.dto.RegisterDiaryResult

interface RegisterDiaryUseCase {

    fun register(command: RegisterDiaryCommand): RegisterDiaryResult
}
