package com.soomsoom.backend.application.service.instructor.command

import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.RegisterInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.PersistenceErrorCode
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.getOrThrow
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterInstructorService(
    private val instructorPort: InstructorPort,
    private val fileUploadFacade: FileUploadFacade,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
    private val fileValidatorPort: FileValidatorPort,
) : RegisterInstructorUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun register(command: RegisterInstructorCommand): RegisterInstructorResult {
        val instructor = Instructor(name = command.name, bio = command.bio)
        val savedInstructor = instructorPort.save(instructor)
        val instructorId = savedInstructor.id ?: throw SoomSoomException(PersistenceErrorCode.ENTITY_SAVE_FAILED)

        val uploadUrl = command.profileImageMetadata?.let { metadata ->
            fileUploadFacade.generateUploadUrls(
                GenerateUploadUrlsRequest(
                    domain = FileDomain.INSTRUCTORS,
                    domainId = instructorId,
                    files = listOf(
                        GenerateUploadUrlsRequest.FileInfo(FileCategory.PROFILE, metadata)
                    )
                )
            ).getOrThrow(FileCategory.PROFILE)
        }

        return RegisterInstructorResult.from(
            instructor = savedInstructor,
            preSignedUrl = uploadUrl?.preSignedUrl,
            fileKey = uploadUrl?.fileKey
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun completeImageUpload(command: CompleteImageUploadCommand) {
        if (!fileValidatorPort.validate(command.fileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }
        val instructor = (
            instructorPort.findById(command.instructorId)
                ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
            )

        val fileUrl = fileUrlResolverPort.resolve(command.fileKey)

        val oldFileKey = instructor.updateProfileImageUrl(url = fileUrl, fileKey = command.fileKey)
        oldFileKey?.let(fileDeleterPort::delete)

        instructorPort.save(instructor)
    }
}
