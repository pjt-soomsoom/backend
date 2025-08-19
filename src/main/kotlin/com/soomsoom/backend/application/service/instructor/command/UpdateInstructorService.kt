package com.soomsoom.backend.application.service.instructor.command

import com.soomsoom.backend.application.port.`in`.instructor.command.UpdateInstructorInfoCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.UpdateInstructorInfoUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.UpdateInstructorProfileImageUrlUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateInstructorService(
    private val instructorPort: InstructorPort,
    private val fileUploadUrlGeneratorPort: FileUploadUrlGeneratorPort,
) : UpdateInstructorInfoUseCase, UpdateInstructorProfileImageUrlUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateInfo(command: UpdateInstructorInfoCommand): FindInstructorResult {
        return instructorPort.findById(command.instructorId)
            ?.apply {
                updateInfo(
                    name = command.name,
                    bio = command.bio
                )
            }
            ?.let(instructorPort::save)
            ?.let(FindInstructorResult::from)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
    }

    override fun updateProfileImageUrl(
        instructorId: Long,
        fileMetadata: ValidatedFileMetadata,
    ): RegisterInstructorResult {
        val instructor = instructorPort.findById(instructorId)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)

        val uploadUrlInfo = fileMetadata ?.let { metaData ->
            fileUploadUrlGeneratorPort.generate(
                filename = metaData.filename,
                domain = FileDomain.INSTRUCTORS,
                domainId = instructorId,
                category = FileCategory.PROFILE,
                contentType = metaData.contentType
            )
        }

        return RegisterInstructorResult.from(
            instructor,
            uploadUrlInfo?.preSignedUrl,
            uploadUrlInfo?.fileKey
        )
    }
}
