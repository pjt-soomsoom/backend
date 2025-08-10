package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InstructorJpaRepository : JpaRepository<InstructorJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): InstructorJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): InstructorJpaEntity?
}
