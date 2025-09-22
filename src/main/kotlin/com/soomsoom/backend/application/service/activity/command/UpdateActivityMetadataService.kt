package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.toActivityResult
import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityMetadataCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityMetadataUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateActivityMetadataService(
    private val activityPort: ActivityPort,
) : UpdateActivityMetadataUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateMetadata(command: UpdateActivityMetadataCommand): ActivityResult {
        val activity = activityPort.findById(command.activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        activity.updateMetadata(command.title, command.descriptions, command.category)
        activity.updateCompletionEffectTexts(command.completionEffectTexts)

        activityPort.save(activity)
        return activityPort.findByIdWithInstructors(command.activityId, command.userId)
            ?.toActivityResult()
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
    }
}
