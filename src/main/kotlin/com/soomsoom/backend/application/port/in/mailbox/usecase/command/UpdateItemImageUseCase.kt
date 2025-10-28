package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementImageUpdateCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementImageCommand
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UpdateAnnouncementFileResult

interface UpdateItemImageUseCase {
    fun updateImage(command: UpdateAnnouncementImageCommand): UpdateAnnouncementFileResult
    fun completeImageUpdate(command: CompleteAnnouncementImageUpdateCommand)
}
