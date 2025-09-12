package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.user.dto.UserPoints
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindUserPointsUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindUserPointsService(
    private val userPort: UserPort
) : FindUserPointsUseCase{

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun findUserPoints(userId: Long): UserPoints {
        val user = userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
        return UserPoints(user.points.value)
    }

}
