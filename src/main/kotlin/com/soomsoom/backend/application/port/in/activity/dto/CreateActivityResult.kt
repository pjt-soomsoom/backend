package com.soomsoom.backend.application.port.`in`.activity.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.common.utils.getOrThrow
import com.soomsoom.backend.domain.upload.type.FileCategory

data class CreateActivityResult(
    val activityId: Long,
    val thumbnailUploadInfo: FileUploadInfo,
    val audioUploadInfo: FileUploadInfo,
) {

    companion object {
        /**
         * 활동 ID와 파일 업로드 URL 맵으로부터 CreateActivityResult 객체
         */
        fun from(activityId: Long, uploadUrls: Map<FileCategory, FileUploadUrl>): CreateActivityResult {
            val thumbnailInfo = uploadUrls.getOrThrow(FileCategory.THUMBNAIL)
            val audioInfo = uploadUrls.getOrThrow(FileCategory.AUDIO)

            return CreateActivityResult(
                activityId = activityId,
                thumbnailUploadInfo = FileUploadInfo(
                    preSignedUrl = thumbnailInfo.preSignedUrl,
                    fileKey = thumbnailInfo.fileKey
                ),
                audioUploadInfo = FileUploadInfo(
                    preSignedUrl = audioInfo.preSignedUrl,
                    fileKey = audioInfo.fileKey
                )
            )
        }
    }
}
