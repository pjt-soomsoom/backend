package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.GrantItemToUserUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GrantItemToUserService(
    private val userPort: UserPort,
) : GrantItemToUserUseCase {
    override fun grantItemToUser(command: GrantItemToUserCommand) {
        val user = userPort.findByIdWithCollections(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        if (!user.hasItem(command.itemId)) {
            user.ownItem(command.itemId)
            userPort.save(user)
        }
    }
}
