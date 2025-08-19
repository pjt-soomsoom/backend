package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityThumbnailCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityThumbnailChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ChangeActivityResult

interface ChangeActivityThumbnailUseCase {
    fun changeThumbnail(command: ChangeActivityThumbnailCommand): ChangeActivityResult
    fun completeThumbnailChange(command: CompleteActivityThumbnailChangeCommand): ActivityResult
}
