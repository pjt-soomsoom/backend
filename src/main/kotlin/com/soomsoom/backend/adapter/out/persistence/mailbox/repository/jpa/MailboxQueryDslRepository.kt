package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.dto.QUserAnnouncementQueryResult
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.dto.UserAnnouncementQueryResult
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.AnnouncementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QAnnouncementJpaEntity.announcementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QUserAnnouncementJpaEntity.userAnnouncementJpaEntity
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MailboxQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findAnnouncements(pageable: Pageable, deletionStatus: DeletionStatus): List<AnnouncementJpaEntity> {
        return queryFactory.selectFrom(announcementJpaEntity)
            .where(
                when (deletionStatus) {
                    DeletionStatus.ACTIVE -> announcementJpaEntity.deletedAt.isNull
                    DeletionStatus.DELETED -> announcementJpaEntity.deletedAt.isNotNull
                    DeletionStatus.ALL -> null
                }
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(announcementJpaEntity.createdAt.desc())
            .fetch()
    }

    fun findUserAnnouncements(userId: Long, pageable: Pageable): List<UserAnnouncementQueryResult> {
        return queryFactory
            .select(
                QUserAnnouncementQueryResult(
                    userAnnouncementJpaEntity.id,
                    userAnnouncementJpaEntity.announcementId,
                    announcementJpaEntity.title,
                    userAnnouncementJpaEntity.receivedAt,
                    userAnnouncementJpaEntity.isRead
                )
            )
            .from(userAnnouncementJpaEntity)
            .join(announcementJpaEntity).on(userAnnouncementJpaEntity.announcementId.eq(announcementJpaEntity.id))
            .where(
                userAnnouncementJpaEntity.userId.eq(userId),
                userAnnouncementJpaEntity.deletedAt.isNull,
                announcementJpaEntity.deletedAt.isNull // 원본이 삭제된 경우도 제외
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(userAnnouncementJpaEntity.receivedAt.desc())
            .fetch()
    }
}
