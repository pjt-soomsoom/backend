package com.soomsoom.backend.adapter.out.persistence.activity.repository

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.ActivityJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.ActivityQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.toDomain
import com.soomsoom.backend.adapter.out.persistence.activity.toJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ActivityPersistenceAdapter(
    private val activityJpaRepository: ActivityJpaRepository,
    private val activityQueryDslRepository: ActivityQueryDslRepository,
) : ActivityPort {

    override fun search(criteria: SearchActivitiesCriteria, pageable: Pageable): Page<ActivityWithInstructorsDto> {
        return activityQueryDslRepository.search(criteria, pageable)
    }

    override fun save(activity: Activity): Activity {
        val entity = activity.id?.let { id ->
            val existingEntity = activityJpaRepository.findByIdOrNull(id)
                ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

            existingEntity.update(activity)
            existingEntity
        } ?: activity.toJpaEntity() // ID가 없으면 새 엔티티를 생성합니다.

        return activityJpaRepository.save(entity).toDomain()
    }

    // author, narrator 정보 포함해서 조회
    override fun findByIdWithInstructors(id: Long, deletionStatus: DeletionStatus): ActivityWithInstructorsDto? {
        return activityQueryDslRepository.findById(id, deletionStatus)
    }

    // 도메인 객체만 조회
    override fun findById(id: Long, deletionStatus: DeletionStatus): Activity? {
        val entity = when (deletionStatus) {
            DeletionStatus.ACTIVE -> activityJpaRepository.findByIdAndDeletedAtIsNull(id)
            DeletionStatus.DELETED -> activityJpaRepository.findByIdAndDeletedAtIsNotNull(id)
            DeletionStatus.ALL -> activityJpaRepository.findByIdOrNull(id)
        }
        return entity?.toDomain()
    }
}
