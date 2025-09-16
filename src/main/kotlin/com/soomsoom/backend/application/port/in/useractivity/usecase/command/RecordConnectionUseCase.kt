package com.soomsoom.backend.application.port.`in`.useractivity.usecase.command

import com.soomsoom.backend.application.port.`in`.useractivity.command.RecordConnectionCommand

interface RecordConnectionUseCase {
    fun recordConnection(command: RecordConnectionCommand)
}
