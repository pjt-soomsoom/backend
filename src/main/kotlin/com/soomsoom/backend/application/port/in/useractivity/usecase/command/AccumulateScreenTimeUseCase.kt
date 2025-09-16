package com.soomsoom.backend.application.port.`in`.useractivity.usecase.command

import com.soomsoom.backend.application.port.`in`.useractivity.command.AccumulateScreenTimeCommand

interface AccumulateScreenTimeUseCase {
    fun accumulate(command: AccumulateScreenTimeCommand)
}
