package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity

data class InstructorWithFollowStatusDto @QueryProjection constructor(
    val instructor: InstructorJpaEntity,
    val isFollowing: Boolean,
)
