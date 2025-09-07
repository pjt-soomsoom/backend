package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.AddUserPointsCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddUserPointsUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddUserPointsService(
    private val userPort: UserPort,
) : AddUserPointsUseCase {
    override fun addUserPoints(command: AddUserPointsCommand): Int {
        val user = (
            userPort.findById(command.userId)
                ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
            )

        user.addPoints(Points(command.amount))

        val savedUser = userPort.save(user)
        return savedUser.points.value
    }
}
