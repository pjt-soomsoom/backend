package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.domain.instructor.model.Instructor

data class RegisterInstructorResponse(
    val id: Long,
) {
    companion object {
        fun from(instructor: Instructor): RegisterInstructorResponse {
            return RegisterInstructorResponse(instructor.id!!)
        }
    }
}
