package com.soomsoom.backend.application.port.out.follow

import com.soomsoom.backend.domain.follow.model.Follow
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FollowPort {
    fun findByFollowerIdAndFolloweeId(followerId: Long, followeeId: Long): Follow?
    fun save(follow: Follow): Follow
    fun delete(follow: Follow)
    fun findFollowingInstructors(userId: Long, pageable: Pageable): Page<Instructor>
}
