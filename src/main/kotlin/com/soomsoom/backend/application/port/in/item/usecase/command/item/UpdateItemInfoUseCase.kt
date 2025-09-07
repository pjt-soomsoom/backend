package com.soomsoom.backend.application.port.`in`.item.usecase.command.item

import com.soomsoom.backend.application.port.`in`.item.command.item.UpdateItemInfoCommand
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto

interface UpdateItemInfoUseCase {
    fun updateInfo(command: UpdateItemInfoCommand): ItemDto
}
