package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class RegisterInstructorRequest(
    @field: NotBlank(message = "강사 이름은 빈 값일 수 없습니다.")
    val name: String?,
    @field:Valid
    val profileImageMetadata: FileMetadata?,
    val bio: String?,
)

fun RegisterInstructorRequest.toCommand() = RegisterInstructorCommand(
    name!!,
    profileImageMetadata?.let {
        ValidatedFileMetadata(
            it.filename!!,
            it.contentType!!
        )
    },
    bio
)
