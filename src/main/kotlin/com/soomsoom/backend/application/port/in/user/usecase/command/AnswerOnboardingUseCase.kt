package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.AnswerOnboardingCommand

interface AnswerOnboardingUseCase {
    fun answer(command: AnswerOnboardingCommand)
}
