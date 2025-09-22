package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivitySummaryResult
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType

data class ActivitySummaryDto @QueryProjection constructor(
    val id: Long,
    val type: String,
    val title: String,
    val category: ActivityCategory,
    val authorId: Long,
    val authorName: String,
    val authorProfileImageUrl: String?,
    val narratorId: Long,
    val narratorName: String,
    val narratorProfileImageUrl: String?,
    val durationInSeconds: Int,
    val thumbnailImageUrl: String?,
    val miniThumbnailImageUrl: String?,
    val isFavorited: Boolean,
)
fun ActivitySummaryDto.toResult(): ActivitySummaryResult {
    return ActivitySummaryResult(
        id = this.id,
        type = ActivityType.valueOf(this.type),
        title = this.title,
        category = this.category,
        author = ActivitySummaryResult.InstructorInfo(
            id = this.authorId,
            name = this.authorName,
            profileImageUrl = this.authorProfileImageUrl
        ),
        narrator = ActivitySummaryResult.InstructorInfo(
            id = this.narratorId,
            name = this.narratorName,
            profileImageUrl = this.narratorProfileImageUrl
        ),
        durationInSeconds = this.durationInSeconds,
        thumbnailImageUrl = this.thumbnailImageUrl,
        miniThumbnailImageUrl = this.miniThumbnailImageUrl,
        isFavorited = this.isFavorited
    )
}
