package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.dto.QUserAnnouncementQueryResult
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.dto.UserAnnouncementQueryResult
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.AnnouncementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QAnnouncementJpaEntity.announcementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QUserAnnouncementJpaEntity.userAnnouncementJpaEntity
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class MailboxQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findAnnouncements(pageable: Pageable, deletionStatus: DeletionStatus): Page<AnnouncementJpaEntity> {
        val content = queryFactory.selectFrom(announcementJpaEntity)
            .where(deletionStatusEq(deletionStatus)) // 헬퍼 메서드 사용
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(announcementJpaEntity.createdAt.desc())
            .fetch()

        val countQuery = queryFactory
            .select(announcementJpaEntity.count())
            .from(announcementJpaEntity)
            .where(deletionStatusEq(deletionStatus)) // 동일한 조건 사용

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findUserAnnouncements(userId: Long, pageable: Pageable): Page<UserAnnouncementQueryResult> {
        val whereClause = userAnnouncementJpaEntity.userId.eq(userId)
            .and(userAnnouncementJpaEntity.deletedAt.isNull)
            .and(announcementJpaEntity.deletedAt.isNull) // 원본이 삭제된 경우도 제외

        val content = queryFactory
            .select(
                QUserAnnouncementQueryResult(
                    userAnnouncementJpaEntity.id,
                    userAnnouncementJpaEntity.announcementId,
                    announcementJpaEntity.title,
                    userAnnouncementJpaEntity.receivedAt,
                    userAnnouncementJpaEntity.read
                )
            )
            .from(userAnnouncementJpaEntity)
            .join(announcementJpaEntity).on(userAnnouncementJpaEntity.announcementId.eq(announcementJpaEntity.id))
            .where(whereClause)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(userAnnouncementJpaEntity.receivedAt.desc())
            .fetch()

        val countQuery = queryFactory
            .select(userAnnouncementJpaEntity.count())
            .from(userAnnouncementJpaEntity)
            .join(announcementJpaEntity).on(userAnnouncementJpaEntity.announcementId.eq(announcementJpaEntity.id))
            .where(whereClause) // 동일한 조건 사용

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun deletionStatusEq(deletionStatus: DeletionStatus): BooleanExpression? {
        return when (deletionStatus) {
            DeletionStatus.ACTIVE -> announcementJpaEntity.deletedAt.isNull
            DeletionStatus.DELETED -> announcementJpaEntity.deletedAt.isNotNull
            DeletionStatus.ALL -> null
        }
    }
}
