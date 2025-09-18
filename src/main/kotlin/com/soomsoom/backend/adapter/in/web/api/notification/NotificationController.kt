package com.soomsoom.backend.adapter.`in`.web.api.notification

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.RegisterUserDeviceRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.ToggleNotificationSettingRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.TrackNotificationClickRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.UnregisterUserDeviceRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.UpdateDiaryTimeRequest
import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationSettingsDto
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.RegisterUserDeviceUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.ToggleNotificationSettingUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.TrackNotificationClickUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.UnregisterUserDeviceUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.UpdateDiaryTimeUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.FindNotificationSettingsUseCase
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val findNotificationSettingsUseCase: FindNotificationSettingsUseCase,
    private val updateDiaryTimeUseCase: UpdateDiaryTimeUseCase,
    private val toggleNotificationSettingUseCase: ToggleNotificationSettingUseCase,
    private val registerUserDeviceUseCase: RegisterUserDeviceUseCase,
    private val unregisterUserDeviceUseCase: UnregisterUserDeviceUseCase,
    private val trackNotificationClickUseCase: TrackNotificationClickUseCase,
) {
    @Operation(summary = "내 알림 설정 조회")
    @GetMapping("/settings")
    fun getMySettings(@AuthenticationPrincipal userDetails: CustomUserDetails): NotificationSettingsDto {
        return findNotificationSettingsUseCase.findByUserId(userDetails.id)
    }

    @Operation(summary = "마음일기 알림 시간 수정")
    @PutMapping("/settings/diary-time")
    @ResponseStatus(HttpStatus.OK)
    fun updateDiaryTime(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody
        request: UpdateDiaryTimeRequest,
    ) {
        updateDiaryTimeUseCase.command(request.toCommand(userDetails.id))
    }

    @Operation(summary = "알림 설정 토글")
    @PatchMapping("/settings/{type}/toggle")
    @ResponseStatus(HttpStatus.OK)
    fun toggleSetting(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable type: NotificationType,
        @Valid @RequestBody
        request: ToggleNotificationSettingRequest,
    ) {
        toggleNotificationSettingUseCase.command(request.toCommand(userDetails.id, type))
    }

    @Operation(summary = "푸시 알림용 디바이스 등록/갱신")
    @PostMapping("/devices")
    @ResponseStatus(HttpStatus.OK)
    fun registerDevice(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody
        request: RegisterUserDeviceRequest,
    ) {
        registerUserDeviceUseCase.command(request.toCommand(userDetails.id))
    }

    @Operation(summary = "푸시 알림용 디바이스 등록 해제 (로그아웃 시)")
    @DeleteMapping("/devices")
    @ResponseStatus(HttpStatus.OK)
    fun unregisterDevice(
        @Valid @RequestBody
        request: UnregisterUserDeviceRequest,
    ) {
        unregisterUserDeviceUseCase.command(request.toCommand())
    }

    @Operation(summary = "푸시 알림 클릭 추적")
    @PostMapping("/track-click")
    @ResponseStatus(HttpStatus.OK)
    fun trackClick(
        @Valid @RequestBody
        request: TrackNotificationClickRequest,
    ) {
        trackNotificationClickUseCase.command(request.toCommand())
    }
}
