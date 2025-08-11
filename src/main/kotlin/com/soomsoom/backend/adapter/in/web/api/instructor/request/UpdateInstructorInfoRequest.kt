package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.application.port.`in`.instructor.command.UpdateInstructorInfoCommand
import jakarta.validation.constraints.NotBlank

data class UpdateInstructorInfoRequest(
    @field: NotBlank(message = "강사 이름은 빈 값일 수 없습니다.")
    val name: String,
    val bio: String?,
)

fun UpdateInstructorInfoRequest.toCommand(instructorId: Long): UpdateInstructorInfoCommand {
    return UpdateInstructorInfoCommand(
        instructorId = instructorId,
        name = name,
        bio = bio
    )
}
