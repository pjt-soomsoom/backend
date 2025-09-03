package com.soomsoom.backend.domain.activity.model

import java.time.LocalDateTime

abstract class Activity(
    open val id: Long?,
    open var title: String,
    open var thumbnailImageUrl: String?,
    open var thumbnailFileKey: String?,
    open var descriptions: List<String>,
    open var authorId: Long,
    open var narratorId: Long,
    open var durationInSeconds: Int,
    open var audioUrl: String?,
    open var audioFileKey: String?,
    open val createdAt: LocalDateTime? = null,
    open val modifiedAt: LocalDateTime? = null,
    open var deletedAt: LocalDateTime? = null,
) {
    abstract val type: ActivityType

    val isDeleted: Boolean
        get() = deletedAt != null

    /**
     * 썸네일 이미지 정보를 업데이트하고, 이전 파일 키를 반환
     */
    fun updateThumbnailImage(url: String, fileKey: String): String? {
        val oldFileKey = this.thumbnailFileKey
        this.thumbnailImageUrl = url
        this.thumbnailFileKey = fileKey
        return oldFileKey
    }

    /**
     * 오디오 정보를 업데이트하고, 이전 파일 키를 반환
     */
    fun updateAudio(url: String, fileKey: String): String? {
        val oldFileKey = this.audioFileKey
        this.audioUrl = url
        this.audioFileKey = fileKey
        return oldFileKey
    }

    /**
     * 메타데이터(제목, 설명 등) 수정
     */
    fun updateMetadata(title: String, descriptions: List<String>) {
        require(title.isNotBlank()) { "제목은 비워둘 수 없습니다." }
        require(descriptions.isNotEmpty()) { "설명은 비워둘 수 없습니다." }

        this.title = title
        this.descriptions = descriptions
    }

    /**
     * 활동을 삭제 처리(soft delete).
     */
    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}
