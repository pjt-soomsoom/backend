package com.soomsoom.backend.application.service.follow.query

import com.soomsoom.backend.application.port.`in`.follow.dto.FollowingInstructorResult
import com.soomsoom.backend.application.port.`in`.follow.usecase.query.FindFollowingInstructorsUseCase
import com.soomsoom.backend.application.port.out.follow.FollowPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindFollowingInstructorsService(
    private val followPort: FollowPort,
) : FindFollowingInstructorsUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    override fun find(userId: Long, pageable: Pageable): Page<FollowingInstructorResult> {
        val instructorPage = followPort.findFollowingInstructors(userId, pageable)
        return instructorPage.map { FollowingInstructorResult.from(it) }
    }
}
