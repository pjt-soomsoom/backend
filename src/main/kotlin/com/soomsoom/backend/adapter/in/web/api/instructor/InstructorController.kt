package com.soomsoom.backend.adapter.`in`.web.api.instructor

import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.RegisterInstructorRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.instructor.response.RegisterInstructorResponse
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class InstructorController(
    private val registerInstructorUseCase: RegisterInstructorUseCase,
) {

    @PostMapping("/instructors")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterInstructorRequest,
    ): RegisterInstructorResponse {
        return RegisterInstructorResponse.from(registerInstructorUseCase.register(request.toCommand()))
    }
}
