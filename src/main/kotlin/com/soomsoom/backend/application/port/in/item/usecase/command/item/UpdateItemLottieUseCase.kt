package com.soomsoom.backend.application.port.`in`.item.usecase.command.item

import com.soomsoom.backend.application.port.`in`.item.command.item.CompleteItemLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult

interface UpdateItemLottieUseCase {
    fun updateLottie(command: UpdateItemLottieCommand): UpdateItemFileResult
    fun completeLottieUpdate(command: CompleteItemLottieUpdateCommand)
}
