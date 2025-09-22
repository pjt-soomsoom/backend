package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityMiniThumbnailCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityMiniThumbnailChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ChangeActivityResult

interface ChangeActivityMiniThumbnailUseCase {
    fun changeMiniThumbnail(command: ChangeActivityMiniThumbnailCommand): ChangeActivityResult
    fun completeThumbnailChange(command: CompleteActivityMiniThumbnailChangeCommand): ActivityResult
}
