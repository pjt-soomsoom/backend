package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.toActivityResult
import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityTimelineCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityTimelineUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateActivityTimelineService(
    private val activityPort: ActivityPort,
) : UpdateActivityTimelineUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateTimeline(command: UpdateActivityTimelineCommand): ActivityResult {
        val activity = activityPort.findById(command.activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        val breathingActivity = activity as? BreathingActivity
            ?: throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)

        breathingActivity.updateTimeline(command.timeline)

        activityPort.save(breathingActivity)
        return activityPort.findByIdWithInstructors(command.activityId)
            ?.toActivityResult()
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
    }
}
