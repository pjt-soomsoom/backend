package com.soomsoom.backend.application.port.`in`.notification.usecase.query.message

import com.soomsoom.backend.application.port.`in`.notification.dto.MessageVariationDto

interface FindMessageVariationsUseCase {
    fun findById(id: Long): MessageVariationDto
}
