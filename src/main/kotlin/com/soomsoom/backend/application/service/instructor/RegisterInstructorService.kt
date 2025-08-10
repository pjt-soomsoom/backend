package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.common.exception.PersistenceErrorCode
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterInstructorService(
    private val instructorPort: InstructorPort,
    private val fileUploadUrlGeneratorPort: FileUploadUrlGeneratorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
) : RegisterInstructorUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun register(command: RegisterInstructorCommand): RegisterInstructorResult {
        val instructor = Instructor(name = command.name, bio = command.bio)
        val savedInstructor = instructorPort.save(instructor)
        val instructorId = savedInstructor.id ?: throw SoomSoomException(PersistenceErrorCode.ENTITY_SAVE_FAILED)

        val uploadUrlInfo = command.profileImageMetadata?.let { metadata ->
            fileUploadUrlGeneratorPort.generate(
                metadata.filename,
                FileDomain.INSTRUCTORS,
                instructorId,
                FileCategory.PROFILE,
                metadata.contentType
            )
        }

        return RegisterInstructorResult.from(
            savedInstructor,
            uploadUrlInfo?.preSignedUrl,
            uploadUrlInfo?.fileKey
        )
    }

    override fun completeImageUpload(command: CompleteImageUploadCommand) {
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
