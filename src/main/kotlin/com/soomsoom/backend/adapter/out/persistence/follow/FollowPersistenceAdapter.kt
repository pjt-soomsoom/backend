package com.soomsoom.backend.adapter.out.persistence.follow

import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.FollowJpaRepository
import com.soomsoom.backend.adapter.out.persistence.follow.repository.jpa.FollowQueryDslRepository
import com.soomsoom.backend.application.port.out.follow.FollowPort
import com.soomsoom.backend.domain.follow.model.Follow
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FollowPersistenceAdapter(
    private val followJpaRepository: FollowJpaRepository,
    private val followQueryDslRepository: FollowQueryDslRepository,
) : FollowPort {
    override fun findByFollowerIdAndFolloweeId(followerId: Long, followeeId: Long): Follow? {
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)?.toDomain()
    }

    override fun save(follow: Follow): Follow {
        val entity = follow.toEntity()
        val savedEntity = followJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun delete(follow: Follow) {
        val entity = follow.toEntity()
        followJpaRepository.delete(entity)
    }

    override fun findFollowingInstructors(userId: Long, pageable: Pageable): Page<Instructor> {
        return followQueryDslRepository.findFollowingInstructors(userId, pageable)
            .map { it.toDomain() }
    }
}
