package com.soomsoom.backend.application.service.upload

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import org.springframework.stereotype.Component

@Component
class FileUploadFacade(
    private val fileUploadUrlGeneratorPort: FileUploadUrlGeneratorPort,
) {
    fun generateUploadUrls(request: GenerateUploadUrlsRequest): Map<FileCategory, FileUploadUrl> {
        return request.files.filter { it.metadata != null }
            .associate { fileInfo ->
                val metadata = fileInfo.metadata!!
                val uploadUrlInfo = fileUploadUrlGeneratorPort.generate(
                    filename = metadata.filename,
                    domain = request.domain,
                    domainId = request.domainId,
                    category = fileInfo.category,
                    contentType = metadata.contentType
                )
                fileInfo.category to uploadUrlInfo
            }
    }
}

data class GenerateUploadUrlsRequest(
    val domain: FileDomain,
    val domainId: Long,
    val files: List<FileInfo>,
) {
    data class FileInfo(
        val category: FileCategory,
        val metadata: ValidatedFileMetadata?,
    )
}
