package com.soomsoom.backend.application.port.`in`.follow.dto

import com.soomsoom.backend.domain.instructor.model.Instructor

data class FollowingInstructorResult(
    val instructorId: Long,
    val name: String,
    val profileImageUrl: String?,
) {
    companion object {
        fun from(instructor: Instructor): FollowingInstructorResult {
            return FollowingInstructorResult(
                instructorId = instructor.id!!,
                name = instructor.name,
                profileImageUrl = instructor.profileImageUrl
            )
        }
    }
}
