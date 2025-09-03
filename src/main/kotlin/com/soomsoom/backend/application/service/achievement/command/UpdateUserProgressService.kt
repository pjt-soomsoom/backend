package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateUserProgressUseCase
import com.soomsoom.backend.application.service.achievement.command.strategy.ProgressUpdateStrategy
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateUserProgressService(
    private val strategies: List<ProgressUpdateStrategy>,
) : UpdateUserProgressUseCase {
    override fun <T : Payload> updateProgress(event: Event<T>) {
        strategies.find { it.supports(event) }?.update(event)
    }
}
