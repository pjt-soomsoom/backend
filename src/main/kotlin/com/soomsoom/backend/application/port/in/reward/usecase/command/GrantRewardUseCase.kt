package com.soomsoom.backend.application.port.`in`.reward.usecase.command

import com.soomsoom.backend.application.port.`in`.reward.command.GrantRewardCommand

interface GrantRewardUseCase {
    fun command(command: GrantRewardCommand)
}
