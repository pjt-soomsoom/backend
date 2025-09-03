package com.soomsoom.backend.application.port.out.upload

import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain

interface FileUploadUrlGeneratorPort {
    fun generate(
        filename: String,
        domain: FileDomain,
        domainId: Long,
        category: FileCategory,
        contentType: String,
    ): FileUploadUrl
}
