package com.soomsoom.backend.application.service.reward.command

import com.soomsoom.backend.application.port.`in`.reward.command.GrantRewardCommand
import com.soomsoom.backend.application.port.`in`.reward.usecase.command.GrantRewardUseCase
import com.soomsoom.backend.application.port.`in`.user.command.AddUserPointsCommand
import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddUserPointsUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.GrantItemToUserUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GrantRewardService(
    private val addUserPointsUseCase: AddUserPointsUseCase,
    private val grantItemToUserUseCase: GrantItemToUserUseCase,
    private val eventPublisher: ApplicationEventPublisher,
    private val itemPort: ItemPort,
) : GrantRewardUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun command(command: GrantRewardCommand) {
        logger.info("Granting reward to userId=${command.userId} from source='${command.source.description}'")
        command.points?.let { points ->
            if (points > 0) {
                addUserPointsUseCase.addUserPoints(AddUserPointsCommand(command.userId, points))
            }
        }

        command.itemId?.let { itemId ->
            itemPort.findById(itemId)
                ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
            grantItemToUserUseCase.grantItemToUser(GrantItemToUserCommand(command.userId, itemId))
        }
    }
}
