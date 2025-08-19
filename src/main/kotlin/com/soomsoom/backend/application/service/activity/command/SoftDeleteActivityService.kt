package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.application.port.`in`.activity.usecase.command.SoftDeleteActivityUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SoftDeleteActivityService(
    private val activityPort: ActivityPort,
) : SoftDeleteActivityUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun softDeleteActivity(activityId: Long) {
        val activity = activityPort.findById(activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        activity.delete()

        activityPort.save(activity)
    }
}
