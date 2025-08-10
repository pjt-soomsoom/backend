package com.soomsoom.backend.application.port.`in`.instructor.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import java.time.LocalDateTime

data class RegisterInstructorResult(
    val instructorId: Long,
    val name: String,
    val bio: String?,
    val preSignedUrl: String?,
    val fileKey: String?,

    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null,
) {
    companion object {
        fun from(
            instructor: Instructor,
            preSignedUrl: String?,
            fileKey: String?,
        ): RegisterInstructorResult {
            return RegisterInstructorResult(
                instructorId = instructor.id ?: throw SoomSoomException(
                    InstructorErrorCode.ID_CANNOT_BE_NULL
                ),
                name = instructor.name,
                bio = instructor.bio,
                preSignedUrl = preSignedUrl,
                fileKey = fileKey,
                createdAt = instructor.createdAt!!,
                modifiedAt = instructor.modifiedAt,
                deletedAt = instructor.deletedAt
            )
        }
    }
}
