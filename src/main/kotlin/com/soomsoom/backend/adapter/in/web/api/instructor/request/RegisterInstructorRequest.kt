package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand

data class RegisterInstructorRequest(
    val name: String,
    val profileImageUrl: String?,
    val bio: String?
)

fun RegisterInstructorRequest.toCommand() = RegisterInstructorCommand(
    name, profileImageUrl, bio
)

