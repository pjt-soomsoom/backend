package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.toDomain
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult

data class ActivityWithInstructorsDto @QueryProjection constructor(
    val activity: ActivityJpaEntity,
    val author: InstructorJpaEntity,
    val narrator: InstructorJpaEntity,
)

fun ActivityWithInstructorsDto.toActivityResult(): ActivityResult {
    val activity = this.activity.toDomain()
    val author = this.author.toDomain()
    val narrator = this.narrator.toDomain()
    return ActivityResult.from(activity, author, narrator)
}
