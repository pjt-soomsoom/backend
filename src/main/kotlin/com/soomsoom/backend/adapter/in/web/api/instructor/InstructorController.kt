package com.soomsoom.backend.adapter.`in`.web.api.instructor

import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.RegisterInstructorRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.SearchInstructorsRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.UpdateInstructorInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.UploadCompleteRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCriteria
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.DeleteInstructorUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.RegisterInstructorUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.UpdateInstructorInfoUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.UpdateInstructorProfileImageUrlUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.FindInstructorByIdUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.SearchInstructorUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class InstructorController(
    private val registerInstructorUseCase: RegisterInstructorUseCase,
    private val findInstructorByIdUseCase: FindInstructorByIdUseCase,
    private val searchInstructorUseCase: SearchInstructorUseCase,
    private val deleteInstructorUseCase: DeleteInstructorUseCase,
    private val updateInstructorInfoUseCase: UpdateInstructorInfoUseCase,
    private val updateInstructorProfileImageUrlUseCase: UpdateInstructorProfileImageUrlUseCase,
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

    /**
     *  ID로 강사 한 명 조회
     */
    @GetMapping("/instructors/{instructorId}")
    @ResponseStatus(HttpStatus.OK)
    fun findInstructorById(
        @PathVariable instructorId: Long,
        @RequestParam(defaultValue = "ACTIVE") deletionStatus: DeletionStatus,
    ): FindInstructorResult {
        return findInstructorByIdUseCase.findById(instructorId, deletionStatus)
    }

    /**
     * 여러 조건으로 강사 목록 조회
     */
    @GetMapping("/instructors")
    @ResponseStatus(HttpStatus.OK)
    fun searchInstructors(
        request: SearchInstructorsRequest,
        pageable: Pageable,
    ): Page<FindInstructorResult> {
        return searchInstructorUseCase.search(request.toCriteria(), pageable)
    }

    /**
     * 강사 삭제
     */
    @DeleteMapping("/instructors/{instructorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteInstructors(
        @PathVariable instructorId: Long,
    ) {
        deleteInstructorUseCase.delete(instructorId)
    }

    /**
     * 강사 이름, 소개 업데이트
     */
    @PatchMapping("/instructors/{instructorId}/info")
    @ResponseStatus(HttpStatus.OK)
    fun updateInstructorsInfo(
        @PathVariable instructorId: Long,
        @Valid @RequestBody
        request: UpdateInstructorInfoRequest,
    ): FindInstructorResult {
        return updateInstructorInfoUseCase.updateInfo(request.toCommand(instructorId))
    }

    /**
     * 강사 프로필 사진 업데이트
     */
    @PatchMapping("/instructors/{instructorId}/profile-image")
    @ResponseStatus(HttpStatus.OK)
    fun updateProfileImageUrl(
        @PathVariable instructorId: Long,
        @Valid @RequestBody
        request: FileMetadata,
    ): RegisterInstructorResult {
        return updateInstructorProfileImageUrlUseCase.updateProfileImageUrl(
            instructorId,
            ValidatedFileMetadata(request.filename!!, request.contentType!!)
        )
    }
}
