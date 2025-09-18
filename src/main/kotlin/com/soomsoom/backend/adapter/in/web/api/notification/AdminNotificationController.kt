package com.soomsoom.backend.adapter.`in`.web.api.notification

import com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin.AddMessageVariationRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin.CreateNotificationTemplateRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin.UpdateMessageVariationRequest
import com.soomsoom.backend.adapter.`in`.web.api.notification.request.admin.UpdateNotificationTemplateRequest
import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationTemplateDto
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.AddMessageVariationUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.DeleteMessageVariationUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.UpdateMessageVariationUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.CreateNotificationTemplateUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.DeleteNotificationTemplateUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.UpdateNotificationTemplateUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.message.FindMessageVariationsUseCase
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.template.FindNotificationTemplatesUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "알림 (관리자)", description = "알림 템플릿 및 메시지 관리 API")
@RestController
@RequestMapping("/admin/notifications")
class AdminNotificationController(
    // Template UseCases
    private val createNotificationTemplateUseCase: CreateNotificationTemplateUseCase,
    private val updateNotificationTemplateUseCase: UpdateNotificationTemplateUseCase,
    private val deleteNotificationTemplateUseCase: DeleteNotificationTemplateUseCase,
    private val findNotificationTemplatesUseCase: FindNotificationTemplatesUseCase,
    // Variation UseCases
    private val addMessageVariationUseCase: AddMessageVariationUseCase,
    private val updateMessageVariationUseCase: UpdateMessageVariationUseCase,
    private val deleteMessageVariationUseCase: DeleteMessageVariationUseCase,
    private val findMessageVariationsUseCase: FindMessageVariationsUseCase,
) {
    // ======== Template API ========
    @Operation(summary = "알림 템플릿 그룹 생성")
    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTemplate(
        @Valid @RequestBody
        request: CreateNotificationTemplateRequest,
    ): Long {
        return createNotificationTemplateUseCase.command(request.toCommand())
    }

    @Operation(summary = "알림 템플릿 그룹 수정")
    @PutMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateTemplate(
        @PathVariable id: Long,
        @Valid @RequestBody
        request: UpdateNotificationTemplateRequest,
    ) {
        updateNotificationTemplateUseCase.command(request.toCommand(id))
    }

    @Operation(summary = "알림 템플릿 그룹 삭제")
    @DeleteMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTemplate(@PathVariable id: Long) {
        deleteNotificationTemplateUseCase.command(id)
    }

    @Operation(summary = "알림 템플릿 그룹 전체 조회")
    @GetMapping("/templates")
    fun findAllTemplates(): List<NotificationTemplateDto> {
        return findNotificationTemplatesUseCase.findAll()
    }

    @Operation(summary = "특정 알림 템플릿 그룹 상세 조회")
    @GetMapping("/templates/{id}")
    fun findTemplateById(@PathVariable id: Long): NotificationTemplateDto {
        return findNotificationTemplatesUseCase.findById(id)
    }

    // ======== Message Variation API ========
    @Operation(summary = "메시지 후보군(Variation) 추가")
    @PostMapping("/variations")
    @ResponseStatus(HttpStatus.CREATED)
    fun addVariation(
        @Valid @RequestBody
        request: AddMessageVariationRequest,
    ): Long {
        return addMessageVariationUseCase.command(request.toCommand())
    }

    @Operation(summary = "메시지 후보군(Variation) 수정")
    @PutMapping("/variations/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateVariation(
        @PathVariable id: Long,
        @Valid @RequestBody
        request: UpdateMessageVariationRequest,
    ) {
        updateMessageVariationUseCase.command(request.toCommand(id))
    }

    @Operation(summary = "메시지 후보군(Variation) 삭제")
    @DeleteMapping("/variations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVariation(@PathVariable id: Long) {
        deleteMessageVariationUseCase.command(id)
    }
}
