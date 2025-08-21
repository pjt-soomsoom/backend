package com.soomsoom.backend.application.port.`in`.instructor.dto

import com.soomsoom.backend.domain.instructor.model.Instructor
import java.time.LocalDateTime

data class FindInstructorResult(
    val instructorId: Long,
    val name: String,
    val bio: String?,
    val profileImageUrl: String?,
    val isFollowing: Boolean?,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun from(instructor: Instructor, isFollowing: Boolean?): FindInstructorResult {
            return FindInstructorResult(
                instructorId = instructor.id!!,
                name = instructor.name,
                bio = instructor.bio,
                profileImageUrl = instructor.profileImageUrl,
                isFollowing = isFollowing,
                createdAt = instructor.createdAt!!,
                modifiedAt = instructor.modifiedAt,
                deletedAt = instructor.deletedAt
            )
        }
    }
}
