package com.soomsoom.backend.adapter.`in`.web.api.mailbox

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UnreadAnnouncementsCountDto
import com.soomsoom.backend.application.port.`in`.mailbox.dto.UserAnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.ReadAnnouncementUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.CountUnreadAnnouncementsUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.FindMyAnnouncementsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mailbox/announcements")
@Tag(name = "우편함 (사용자)", description = "사용자 우편함 관련 API")
class MailboxController(
    private val findMyAnnouncementsUseCase: FindMyAnnouncementsUseCase,
    private val readAnnouncementUseCase: ReadAnnouncementUseCase,
    private val countUnreadAnnouncementsUseCase: CountUnreadAnnouncementsUseCase,
) {
    @GetMapping
    @Operation(summary = "내 우편함 조회", description = "로그인한 사용자의 우편함(공지 목록)을 페이지네이션으로 조회합니다. 삭제된 공지는 보이지 않습니다.")
    fun findMyAnnouncements(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails,
        @ParameterObject pageable: Pageable,
    ): Page<UserAnnouncementDto> {
        return findMyAnnouncementsUseCase.find(userDetails.id, pageable)
    }

    @PostMapping("/{userAnnouncementId}")
    @Operation(summary = "공지 읽음 처리 및 상세 조회", description = "특정 공지를 '읽음' 상태로 변경하고, 해당 공지의 상세 내용을 반환합니다.")
    fun readAnnouncement(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Parameter(description = "읽음 처리할 사용자 공지 ID (목록 조회 시 받은 `userAnnouncementId`)")
        @PathVariable
        userAnnouncementId: Long,
    ): AnnouncementDto {
        return readAnnouncementUseCase.command(userDetails.id, userAnnouncementId)
    }

    @GetMapping("/unread-count")
    @Operation(summary = "안 읽은 공지 개수 조회", description = "홈 화면 등에 뱃지를 표시하기 위해, 안 읽은 공지의 총 개수를 조회합니다.")
    fun countUnreadAnnouncements(@Parameter(hidden = true) @AuthenticationPrincipal userDetails: CustomUserDetails): UnreadAnnouncementsCountDto {
        return countUnreadAnnouncementsUseCase.count(userDetails.id)
    }
}
