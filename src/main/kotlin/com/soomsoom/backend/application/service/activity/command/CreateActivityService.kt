package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityUploadCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateBreathingActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateMeditationActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateSoundEffectActivityCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.CreateActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.CreateActivityUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.PersistenceErrorCode
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.SoundEffectActivity
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateActivityService(
    private val activityPort: ActivityPort,
    private val instructorPort: InstructorPort,
    private val fileUrlResolverPort: FileUrlResolverPort,
    private val fileDeleterPort: FileDeleterPort,
    private val fileValidatorPort: FileValidatorPort,
    private val fileUploadFacade: FileUploadFacade,
) : CreateActivityUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateActivityCommand): CreateActivityResult {
        validateInstructorsExist(command.authorId, command.narratorId)

        val initialActivity = createInitialActivityFrom(command)

        val savedActivity = activityPort.save(initialActivity)
        val activityId = savedActivity.id ?: throw SoomSoomException(PersistenceErrorCode.ENTITY_SAVE_FAILED)

        // 업로드할 presignedUrl과 FileKey 생성
        val uploadUrls = fileUploadFacade.generateUploadUrls(
            GenerateUploadUrlsRequest(
                domain = FileDomain.ACTIVITIES,
                domainId = activityId,
                files = listOf(
                    GenerateUploadUrlsRequest.FileInfo(FileCategory.THUMBNAIL, command.thumbnailImageMetadata),
                    GenerateUploadUrlsRequest.FileInfo(FileCategory.AUDIO, command.audioMetadata),
                    GenerateUploadUrlsRequest.FileInfo(FileCategory.MINI_THUMBNAIL, command.miniThumbnailImageMetadata)
                )
            )
        )

        return CreateActivityResult.from(activityId, uploadUrls)
    }

    /**
     * presigendUrl로 파일 업로드 후 백엔드에서 db에 파일 경로 업데이트
     */
    @PreAuthorize("hasRole('ADMIN')")
    override fun completeUpload(command: CompleteActivityUploadCommand) {
        validateFileKeys(command)
        val activity = findActivityOrThrow(command.activityId)
        updateActivityWithUploadedFiles(activity, command)
        activityPort.save(activity)
    }

    // author와 narrator가 있는지 검증
    private fun validateInstructorsExist(authorId: Long, narratorId: Long) {
        // author와 narrator 가 같으면 한 번만 조회
        if (authorId == narratorId) {
            instructorPort.findById(authorId)
                ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
        } else {
            instructorPort.findById(authorId)
                ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
            instructorPort.findById(narratorId)
                ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
        }
    }

    /**
     * CreateActivityCommand를 기반으로 초기 Activity 도메인 객체를 생성
     */

    private fun createInitialActivityFrom(command: CreateActivityCommand): Activity {
        return when (command) {
            is CreateBreathingActivityCommand -> BreathingActivity(
                id = null, title = command.title, descriptions = command.descriptions,
                authorId = command.authorId, narratorId = command.narratorId,
                durationInSeconds = command.durationInSeconds, thumbnailImageUrl = null,
                thumbnailFileKey = null, audioUrl = null, audioFileKey = null,
                timeline = command.timeline, category = command.category,
                miniThumbnailImageUrl = null,
                miniThumbnailFileKey = null,
                completionEffectTexts = command.completionEffectTexts
            )
            is CreateMeditationActivityCommand -> MeditationActivity(
                id = null, title = command.title, descriptions = command.descriptions,
                authorId = command.authorId, narratorId = command.narratorId,
                durationInSeconds = command.durationInSeconds, thumbnailImageUrl = null,
                thumbnailFileKey = null, audioUrl = null, audioFileKey = null, category = command.category,
                miniThumbnailImageUrl = null,
                miniThumbnailFileKey = null,
                completionEffectTexts = command.completionEffectTexts
            )
            is CreateSoundEffectActivityCommand -> SoundEffectActivity(
                id = null, title = command.title, descriptions = command.descriptions,
                authorId = command.authorId, narratorId = command.narratorId,
                durationInSeconds = command.durationInSeconds, thumbnailImageUrl = null,
                thumbnailFileKey = null, audioUrl = null, audioFileKey = null, category = command.category,
                miniThumbnailImageUrl = null,
                miniThumbnailFileKey = null,
                completionEffectTexts = command.completionEffectTexts
            )
        }
    }

    /**
     * S3에 업로드한 fileKey가 맞는지 확인
     */
    private fun validateFileKeys(command: CompleteActivityUploadCommand) {
        if (command.miniThumbnailFileKey != null && !fileValidatorPort.validate(command.miniThumbnailFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }
        if (!fileValidatorPort.validate(command.thumbnailFileKey) || !fileValidatorPort.validate(command.audioFileKey)) {
            throw SoomSoomException(UploadErrorCode.FILE_KEY_MISMATCH)
        }
    }

    private fun findActivityOrThrow(activityId: Long): Activity {
        return activityPort.findById(activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
    }

    private fun updateActivityWithUploadedFiles(activity: Activity, command: CompleteActivityUploadCommand) {
        val thumbnailUrl = fileUrlResolverPort.resolve(command.thumbnailFileKey)
        val audioUrl = fileUrlResolverPort.resolve(command.audioFileKey)
        val oldMiniThumbnailKey = command.miniThumbnailFileKey?.let {
            val miniThumbnailUrl = fileUrlResolverPort.resolve(command.miniThumbnailFileKey)
            activity.updateMiniThumbnailImage(url = miniThumbnailUrl, fileKey = it)
        }

        val oldThumbnailKey = activity.updateThumbnailImage(url = thumbnailUrl, fileKey = command.thumbnailFileKey)
        val oldAudioKey = activity.updateAudio(url = audioUrl, fileKey = command.audioFileKey)

        oldThumbnailKey?.let(fileDeleterPort::delete)
        oldAudioKey?.let(fileDeleterPort::delete)
        oldMiniThumbnailKey?.let(fileDeleterPort::delete)
    }
}
