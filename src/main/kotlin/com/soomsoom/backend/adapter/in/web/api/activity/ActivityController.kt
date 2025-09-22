package com.soomsoom.backend.adapter.`in`.web.api.activity

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.CompleteActivityUploadRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.CreateActivityRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.SearchActivitiesRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.ChangeActivityAudioRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.ChangeActivityMiniThumbnailRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.ChangeActivityThumbnailRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.CompleteActivityAudioChangeRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.CompleteActivityMiniThumbnailRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.CompleteActivityThumbnailChangeRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.change.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.toCriteria
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.update.UpdateActivityMetadataRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.update.UpdateActivityTimelineRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.update.toCommand
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivitySummaryResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ChangeActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.CreateActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityAudioUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityMiniThumbnailUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityThumbnailUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.CreateActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.SoftDeleteActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityMetadataUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityTimelineUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.FindActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.SearchActivitiesUseCase
import com.soomsoom.backend.application.port.`in`.favorite.command.ToggleFavoriteCommand
import com.soomsoom.backend.application.port.`in`.favorite.dto.ToggleFavoriteResult
import com.soomsoom.backend.application.port.`in`.favorite.usecase.command.ToggleFavoriteUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ActivityController(
    private val createActivityUseCase: CreateActivityUseCase,
    private val findActivityUseCase: FindActivityUseCase,
    private val searchActivitiesUseCase: SearchActivitiesUseCase,
    private val updateActivityMetadataUseCase: UpdateActivityMetadataUseCase,
    private val updateActivityTimelineUseCase: UpdateActivityTimelineUseCase,
    private val changeActivityThumbnailUseCase: ChangeActivityThumbnailUseCase,
    private val changeActivityAudioUseCase: ChangeActivityAudioUseCase,
    private val softDeleteActivityUseCase: SoftDeleteActivityUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val changeActivityMiniThumbnailUseCase: ChangeActivityMiniThumbnailUseCase,
) {

    /**
     * activity 생성
     */
    @PostMapping("/activities")
    @ResponseStatus(HttpStatus.CREATED)
    fun createActivity(
        @Valid @RequestBody
        request: CreateActivityRequest,
    ): CreateActivityResult {
        return createActivityUseCase.create(request.toCommand())
    }

    /**
     * 파일 업로드 완료 처리
     */
    @PostMapping("/activities/{activityId}/complete-upload")
    @ResponseStatus(HttpStatus.OK)
    fun completeUpload(
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: CompleteActivityUploadRequest,
    ) {
        createActivityUseCase.completeUpload(request.toCommand(activityId))
    }

    /**
     * activity 단 건 조회
     */
    @GetMapping("/activities/{activityId}")
    @ResponseStatus(HttpStatus.OK)
    fun findActivity(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @RequestParam(required = false) deletionStatus: DeletionStatus?,
    ): ActivityResult {
        return findActivityUseCase.findActivity(activityId, userDetails.id, deletionStatus ?: DeletionStatus.ACTIVE)
    }

    /**
     * activity 다 건 조회 페이징 적용
     */
    @GetMapping("/activities")
    fun searchActivities(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @ModelAttribute request: SearchActivitiesRequest,
        pageable: Pageable,
    ): Page<ActivitySummaryResult> {
        return searchActivitiesUseCase.search(request.toCriteria(userDetails.id), pageable)
    }

    /**
     * 메타데이터(제목, 설명) 수정
     */
    @PatchMapping("/activities/{activityId}/metadata")
    @ResponseStatus(HttpStatus.OK)
    fun updateMetadata(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: UpdateActivityMetadataRequest,
    ): ActivityResult {
        return updateActivityMetadataUseCase.updateMetadata(request.toCommand(activityId, userDetails.id))
    }

    /**
     * 타임라인만 수정
     */
    @PatchMapping("/activities/{activityId}/timeline")
    @ResponseStatus(HttpStatus.OK)
    fun updateTimeline(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: UpdateActivityTimelineRequest,
    ): ActivityResult {
        return updateActivityTimelineUseCase.updateTimeline(request.toCommand(activityId, userDetails.id))
    }

    /**
     * 썸네일 교체 시작 (Presigned URL 발급)
     */
    @PostMapping("/activities/{activityId}/thumbnail")
    @ResponseStatus(HttpStatus.OK)
    fun changeThumbnail(
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: ChangeActivityThumbnailRequest,
    ): ChangeActivityResult {
        return changeActivityThumbnailUseCase.changeThumbnail(request.toCommand(activityId))
    }

    /**
     * 썸네일 교체 완료
     */
    @PostMapping("/activities/{activityId}/thumbnail/complete-upload")
    @ResponseStatus(HttpStatus.OK)
    fun completeThumbnailChange(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: CompleteActivityThumbnailChangeRequest,
    ): ActivityResult {
        return changeActivityThumbnailUseCase.completeThumbnailChange(request.toCommand(activityId, userDetails.id))
    }

    /**
     * 미니 썸네일 교체 시작 (presigend URL 발급)
     */
    @PostMapping("/activities/{activityId}/mini-thumbnail")
    @ResponseStatus(HttpStatus.OK)
    fun changeMiniThumbnail(
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: ChangeActivityMiniThumbnailRequest,
    ): ChangeActivityResult {
        return changeActivityMiniThumbnailUseCase.changeMiniThumbnail(request.toCommand(activityId))
    }

    /**
     * 썸네일 교체 완료
     */
    @PostMapping("/activities/{activityId}/mini-thumbnail/complete-upload")
    @ResponseStatus(HttpStatus.OK)
    fun completeMiniThumbnailChange(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: CompleteActivityMiniThumbnailRequest,
    ): ActivityResult {
        return changeActivityMiniThumbnailUseCase.completeThumbnailChange(request.toCommand(activityId, userDetails.id))
    }

    /**
     * 오디오 교체 시작 (Presigned URL 발급)
     */
    @PostMapping("/activities/{activityId}/audio")
    @ResponseStatus(HttpStatus.OK)
    fun changeAudio(
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: ChangeActivityAudioRequest,
    ): ChangeActivityResult {
        return changeActivityAudioUseCase.changeAudio(request.toCommand(activityId))
    }

    /**
     * 오디오 교체 완료
     */
    @PostMapping("/activities/{activityId}/audio/complete-upload")
    @ResponseStatus(HttpStatus.OK)
    fun completeAudioChange(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable activityId: Long,
        @Valid @RequestBody
        request: CompleteActivityAudioChangeRequest,
    ): ActivityResult {
        return changeActivityAudioUseCase.completeAudioChange(request.toCommand(activityId, userDetails.id))
    }

    /**
     * Soft Delete 처리
     */
    @DeleteMapping("/activities/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun softDeleteActivity(@PathVariable activityId: Long) {
        softDeleteActivityUseCase.softDeleteActivity(activityId)
    }

    /**
     * 활동 즐겨찾기 토글 (추가/제거)
     */
    @PostMapping("/activities/{activityId}/favorite")
    @ResponseStatus(HttpStatus.OK)
    fun toggleFavorite(
        @PathVariable activityId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ToggleFavoriteResult {
        val command = ToggleFavoriteCommand(userId = userDetails.id, activityId = activityId)
        return toggleFavoriteUseCase.toggle(command)
    }
}
