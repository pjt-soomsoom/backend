package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload

interface UpdateUserProgressUseCase {
    fun<T : Payload> updateProgress(event: Event<T>)
}
