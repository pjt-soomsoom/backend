package com.soomsoom.backend.application.port.`in`.item.usecase.command

import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.CompleteItemLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemImageCommand
import com.soomsoom.backend.application.port.`in`.item.command.UpdateItemLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateItemFileResult

interface UpdateItemUseCase {
    fun updateItem(command: UpdateItemCommand): ItemDto
    fun updateItemImage(command: UpdateItemImageCommand): UpdateItemFileResult
    fun completeItemImageUpdate(command: CompleteItemImageUpdateCommand)
    fun updateItemLottie(command: UpdateItemLottieCommand): UpdateItemFileResult
    fun completeItemLottieUpdate(command: CompleteItemLottieUpdateCommand)
}
