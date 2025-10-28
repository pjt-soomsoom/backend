package com.soomsoom.backend.adapter.`in`.web.api.mailbox

import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.CompleteAnnouncementImageUpdateRequest
import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.CompleteAnnouncementUploadRequest
import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.CreateAnnouncementRequest
import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.UpdateAnnouncementInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.mailbox.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.mailbox.command.CompleteAnnouncementUploadCommand
import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementImageCommand
import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.dto.CreateAnnouncementResult
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UpdateAnnouncementFileResult
import com.soomsoom.backend.application.port.`in`.mailbox.query.FindAnnouncementsCriteria
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.CreateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DeleteAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.UpdateAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementDetailsUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindAnnouncementsUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.common.DeletionStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/mailbox/announcements")
@Tag(name = "우편함 (관리자)", description = "공지사항 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
class AdminMailboxController(
    private val createAnnouncementUseCase: CreateAnnouncementUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val findAnnouncementsUseCase: FindAnnouncementsUseCase,
    private val findAnnouncementDetailsUseCase: FindAnnouncementDetailsUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공지 생성", description = "새로운 공지를 생성합니다.")
    fun createAnnouncement(
        @Valid @RequestBody
        request: CreateAnnouncementRequest,
    ): CreateAnnouncementResult {
        return createAnnouncementUseCase.create(request.toCommand())
    }

    /**
     * 생성 후 파일 업로드 완료 처리
     */
    @PostMapping("/{announcementId}/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "공지 이미지 업로드 완료 요청",
        description = "클라이언트가 Presigned URL을 통해 이미지 업로드를 완료한 후, " +
            "해당 이미지의 파일 키(file key)를 서버에 알려주어 공지사항과 이미지를 최종 연결(확정)합니다."
    )
    fun completeAnnouncementUpload(
        @Parameter(description = "이미지를 연결할 공지 ID", example = "1")
        @PathVariable
        announcementId: Long,
        @Valid @RequestBody
        request: CompleteAnnouncementUploadRequest,
    ) {
        val command = CompleteAnnouncementUploadCommand(
            announcementId = announcementId,
            imageFileKey = request.imageFileKey!!
        )
        createAnnouncementUseCase.completeUpload(command)
    }

    /**
     * 공지 사항 메타 데이터 수정
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "공지 메타 데이터 수정", description = "기존 공지의 제목과 내용을 수정합니다.")
    fun updateAnnouncement(
        @Parameter(description = "수정할 공지 ID") @PathVariable id: Long,
        @Valid @RequestBody
        request: UpdateAnnouncementInfoRequest,
    ) {
        updateAnnouncementUseCase.updateInfo(request.toCommand(id))
    }

    /**
     * 이미지 업데이트 요청
     */
    @PutMapping("/{announcementId}image")
    @Operation(
        summary = "공지 이미지 업데이트 요청 (Presigned URL 생성)",
        description = "기존 공지 이미지를 교체하기 위해, 새로운 이미지를 업로드할 Presigned URL을 발급받습니다."
    )
    fun updateAnnouncementImage(
        @Parameter(description = "이미지를 교체할 공지 ID", example = "1")
        @PathVariable
        announcementId: Long,

        @Valid @RequestBody
        request: FileMetadata,
    ): UpdateAnnouncementFileResult {
        val command = UpdateAnnouncementImageCommand(
            announcementId = announcementId,
            imageMetadata = ValidatedFileMetadata(request.filename!!, request.contentType!!)
        )
        return updateAnnouncementUseCase.updateImage(command)
    }

    /**
     * 이미지 업데이트 완료 요청
     */
    @PostMapping("/{announcementId}/image/upload-complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "공지 이미지 업데이트 완료 요청",
        description = "클라이언트가 Presigned URL을 통해 새 이미지 업로드를 완료한 후, " +
            "해당 이미지의 파일 키(file key)를 서버에 알려주어 공지사항의 이미지를 최종 교체합니다."
    )
    fun completeAnnouncementImageUpdate(
        @Parameter(description = "이미지를 교체할 공지 ID", example = "1")
        @PathVariable
        announcementId: Long,

        @Valid @RequestBody
        request: CompleteAnnouncementImageUpdateRequest,
    ) {
        val command = request.toCommand(announcementId)
        updateAnnouncementUseCase.completeImageUpdate(command)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "공지 삭제", description = "공지를 삭제(soft-delete) 처리합니다.")
    fun deleteAnnouncement(@Parameter(description = "삭제할 공지 ID") @PathVariable id: Long) {
        deleteAnnouncementUseCase.command(id)
    }

    @GetMapping
    @Operation(summary = "공지 목록 조회", description = "페이지네이션을 사용하여 공지 목록을 조회합니다. 삭제 상태로 필터링할 수 있습니다.")
    fun findAnnouncements(
        @ParameterObject pageable: Pageable,
        @Parameter(description = "삭제 상태 필터 (ACTIVE, DELETED, ALL)")
        @RequestParam(defaultValue = "ACTIVE")
        deletionStatus: DeletionStatus,
    ): Page<AnnouncementDto> {
        val criteria = FindAnnouncementsCriteria(pageable, deletionStatus)
        return findAnnouncementsUseCase.find(criteria)
    }

    @GetMapping("/{id}")
    @Operation(summary = "공지 상세 조회", description = "특정 공지의 상세 내용을 조회합니다. 삭제된 공지도 조회 가능합니다.")
    fun findAnnouncementDetails(@Parameter(description = "조회할 공지 ID") @PathVariable id: Long): AnnouncementDto {
        return findAnnouncementDetailsUseCase.find(id)
    }
}
