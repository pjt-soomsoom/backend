package com.soomsoom.backend.adapter.out.persistence.instructor

import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class InstructorPersistenceAdapter(
    private val instructorJpaRepository: InstructorJpaRepository,
) : InstructorPort {
    override fun findById(id: Long): Instructor? {
        return instructorJpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findAll(): List<Instructor> {
        return instructorJpaRepository.findAll().map { it.toDomain() }
    }

    override fun save(instructor: Instructor): Instructor {
        return if (instructor.id == null) {
            instructorJpaRepository.save(InstructorJpaEntity.from(instructor)).toDomain()
        } else {
            val entity = instructorJpaRepository.findByIdOrNull(instructor.id)
                ?: throw SoomSoomException(InstructorErrorCode.INSTRUCTOR_NOT_FOUND)
            entity.update(instructor)
            entity.toDomain()
        }
    }
}
