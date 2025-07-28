package com.soomsoom.backend.application.port.`in`.instructor.dto

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor

data class RegisterInstructorResult(
    val instructorId: Long,
    val preSignedUrl: String,
    val fileKey: String,
) {
    companion object {
        fun from(
            instructor: Instructor,
            preSignedUrl: String,
            fileKey: String,
        ): RegisterInstructorResult {
            return RegisterInstructorResult(
                instructorId = instructor.id ?: throw SoomSoomException(
                    InstructorErrorCode.INSTRUCTOR_ID_CANNOT_BE_NULL
                ),
                preSignedUrl = preSignedUrl,
                fileKey = fileKey
            )
        }
    }
}
