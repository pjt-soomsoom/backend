package com.soomsoom.backend.adapter.`in`.web.api.instructor

import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.RegisterInstructorRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.UploadCompleteRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
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
        @Valid @RequestBody
        request: RegisterInstructorRequest,
    ): RegisterInstructorResult {
        return registerInstructorUseCase.register(request.toCommand())
    }

    @PostMapping("/instructors/{instructorId}/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeProfileImageUpload(
        @PathVariable instructorId: Long,
        @Valid @RequestBody
        request: UploadCompleteRequest,
    ) {
        return registerInstructorUseCase.completeImageUpload(CompleteImageUploadCommand(instructorId, request.fileKey))
    }
}
