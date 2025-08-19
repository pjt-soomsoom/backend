package com.soomsoom.backend.application.port.`in`.activity.usecase.command

import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityAudioCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityAudioChangeCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ChangeActivityResult

interface ChangeActivityAudioUseCase {
    fun changeAudio(command: ChangeActivityAudioCommand): ChangeActivityResult
    fun completeAudioChange(command: CompleteActivityAudioChangeCommand): ActivityResult
}
