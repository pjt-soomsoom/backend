package com.soomsoom.backend.adapter.out.persistence.instructor

import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class InstructorPersistenceAdapter(
    private val instructorJpaRepository: InstructorJpaRepository,
    private val instructorQueryDslRepository: InstructorQueryDslRepository,
) : InstructorPort {
    override fun findById(instructorId: Long, deletionStatus: DeletionStatus): Instructor? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> instructorJpaRepository.findByIdAndDeletedAtIsNull(instructorId)
            DeletionStatus.DELETED -> instructorJpaRepository.findByIdAndDeletedAtIsNotNull(instructorId)
            DeletionStatus.ALL -> instructorJpaRepository.findByIdOrNull(instructorId)
        }
        return entity?.toDomain()
    }

    override fun search(criteria: InstructorSearchCriteria, pageable: Pageable): Page<Instructor> {
        return instructorQueryDslRepository.search(criteria, pageable).map { it.toDomain() }
    }

    override fun save(instructor: Instructor): Instructor {
        return if (instructor.id == null) {
            instructorJpaRepository.save(InstructorJpaEntity.from(instructor)).toDomain()
        } else {
            val entity = instructorJpaRepository.findByIdOrNull(instructor.id)
                ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
            entity.update(instructor)
            entity.toDomain()
        }
    }
}
