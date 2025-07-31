package com.soomsoom.backend.application.port.out.upload

import com.soomsoom.backend.adapter.out.s3.type.FileCategory
import com.soomsoom.backend.adapter.out.s3.type.FileDomain
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl

interface FileUploadUrlGeneratorPort {
    fun generate(
        filename: String,
        domain: FileDomain,
        domainId: Long,
        category: FileCategory,
        contentType: String,
    ): FileUploadUrl
}
