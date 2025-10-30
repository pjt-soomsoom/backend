package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementUploadCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.CreateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.mailbox.dto.CreateAnnouncementResult

interface CreateAnnouncementUseCase {
    fun create(command: CreateAnnouncementCommand): CreateAnnouncementResult
    fun completeUpload(command: CompleteAnnouncementUploadCommand)
}
