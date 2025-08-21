package com.soomsoom.backend.application.port.`in`.follow.usecase.query

import com.soomsoom.backend.application.port.`in`.follow.dto.FollowingInstructorResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindFollowingInstructorsUseCase {
    fun find(userId: Long, pageable: Pageable): Page<FollowingInstructorResult>
}
