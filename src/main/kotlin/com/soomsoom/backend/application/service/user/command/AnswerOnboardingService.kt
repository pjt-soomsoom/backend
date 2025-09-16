package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.AnswerOnboardingCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AnswerOnboardingUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AnswerOnboardingService(
    private val userPort: UserPort,
) : AnswerOnboardingUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun answer(command: AnswerOnboardingCommand) {
        val user = userPort.findById(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        user.answerOnboardingQuestions(command.focusGoal, command.dailyDuration)

        userPort.save(user)
    }
}
