package com.soomsoom.backend.application.service.follow.command

import com.soomsoom.backend.application.port.`in`.follow.command.ToggleFollowCommand
import com.soomsoom.backend.application.port.`in`.follow.dto.ToggleFollowResult
import com.soomsoom.backend.application.port.`in`.follow.usecase.command.ToggleFollowUseCase
import com.soomsoom.backend.application.port.out.follow.FollowPort
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.follow.model.Follow
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ToggleFollowService(
    private val followPort: FollowPort,
    private val instructorPort: InstructorPort,
) : ToggleFollowUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == command.followerId")
    override fun toggle(command: ToggleFollowCommand): ToggleFollowResult {
        instructorPort.findById(command.followeeId)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)

        val existingFollow = followPort.findByFollowerIdAndFolloweeId(command.followerId, command.followeeId)

        return existingFollow?.let { follow ->
            // 팔로우가 이미 존재하면(null이 아니면) 삭제
            followPort.delete(follow)
            ToggleFollowResult(
                followeeId = command.followeeId,
                isFollowing = false
            )
        } ?: run {
            // 팔로우가 없으면(null이면) 새로 생성하여 저장
            val newFollow = Follow(
                followerId = command.followerId,
                followeeId = command.followeeId
            )
            followPort.save(newFollow)
            ToggleFollowResult(
                followeeId = command.followeeId,
                isFollowing = true
            )
        }
    }
}
