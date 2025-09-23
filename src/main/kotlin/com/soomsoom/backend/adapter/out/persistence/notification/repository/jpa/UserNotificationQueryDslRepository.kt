package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QUserAnnouncementJpaEntity.userAnnouncementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.dto.QUserNotificationPushQueryResult
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.dto.UserNotificationPushQueryResult
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserDeviceJpaEntity.userDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserNotificationSettingJpaEntity.userNotificationSettingJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity.userJpaEntity
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.QInactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity.connectionLogJpaEntity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class UserNotificationQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findDevicesByUserIds(userIds: List<Long>): List<UserDeviceJpaEntity> {
        if (userIds.isEmpty()) {
            return emptyList()
        }

        return queryFactory
            .selectFrom(userDeviceJpaEntity)
            .where(userDeviceJpaEntity.userId.`in`(userIds))
            .fetch()
    }

    fun findDiaryReminderTargetUserIds(
        targetTime: LocalTime,
        yesterdayStart: LocalDateTime,
        yesterdayEnd: LocalDateTime,
        todayStart: LocalDateTime,
        todayEnd: LocalDateTime,
        pageNumber: Int,
        pageSize: Int,
    ): List<Long> {
        val qDiary = QDiaryJpaEntity("qDiary")
        val qActivityLog = QActivityCompletionLogJpaEntity("qActivityLog")
        val qConnectionLog = QConnectionLogJpaEntity("qConnectionLog")

        // 오늘 일기를 작성했는지 확인하는 서브쿼리 (Boolean)
        return queryFactory
            .select(userNotificationSettingJpaEntity.userId)
            .from(userNotificationSettingJpaEntity)
            .leftJoin(qConnectionLog).on(
                qConnectionLog.userId.eq(userNotificationSettingJpaEntity.userId),
                qConnectionLog.createdAt.between(yesterdayStart, yesterdayEnd)
            )
            .leftJoin(qDiary).on(
                qDiary.userId.eq(userNotificationSettingJpaEntity.userId),
                qDiary.createdAt.between(todayStart, todayEnd)
            )
            .leftJoin(qActivityLog).on(
                qActivityLog.userId.eq(userNotificationSettingJpaEntity.userId),
                qActivityLog.createdAt.between(todayStart, todayEnd),
                qActivityLog.activityType.`in`(ActivityType.BREATHING, ActivityType.MEDITATION)
            )
            .where(
                userNotificationSettingJpaEntity.diaryNotificationEnabled.isTrue,
                userNotificationSettingJpaEntity.diaryNotificationTime.eq(targetTime),
                qConnectionLog.id.isNotNull, // 어제 접속 기록이 있는 사용자 (INNER JOIN 효과)
                qDiary.id.isNull.or(qActivityLog.id.isNull) // NOT (A AND B) 와 동일
            )
            .groupBy(userNotificationSettingJpaEntity.userId) // JOIN으로 인한 중복 제거
            .orderBy(userNotificationSettingJpaEntity.userId.asc())
            .offset((pageNumber * pageSize).toLong())
            .limit(pageSize.toLong())
            .fetch()
    }

    /**
     * 재방문 유도(Re-engagement) 알림 발송 대상자를 조회
     * 1. 마지막 접속일이 주어진 조건(inactivityConditions)에 해당하는 사용자를 찾음
     * 2. 재방문 유도 알림 수신을 활성화(`reEngagementNotificationEnabled = true`)한 사용자만 필터링
     */
    fun findReEngagementTargets(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto> {
        if (inactivityConditions.isEmpty()) {
            return emptyList()
        }

        val lastCreatedAt = connectionLogJpaEntity.createdAt.max()
        val inactiveDaysExpression = buildInactiveDaysCaseExpression(inactivityConditions, lastCreatedAt)
        val havingExpression = buildHavingExpression(inactivityConditions, lastCreatedAt)

        return queryFactory
            .select(
                QInactiveUserAdapterDto(
                    connectionLogJpaEntity.userId,
                    inactiveDaysExpression
                )
            )
            .from(connectionLogJpaEntity)
            .innerJoin(userNotificationSettingJpaEntity)
            .on(connectionLogJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId))
            .where(
                userNotificationSettingJpaEntity.reEngagementNotificationEnabled.isTrue,
                connectionLogJpaEntity.deletedAt.isNull
            )
            .groupBy(connectionLogJpaEntity.userId)
            .having(havingExpression)
            .orderBy(connectionLogJpaEntity.userId.asc())
            .offset((pageNumber * pageSize).toLong())
            .limit(pageSize.toLong())
            .fetch()
    }

    private fun buildInactiveDaysCaseExpression(
        conditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        expression: DateTimeExpression<LocalDateTime>,
    ): NumberExpression<Int> {
        var caseWhenChain: CaseBuilder.Cases<Int, NumberExpression<Int>>? = null
        conditions.forEach { (days, range) ->
            val condition = expression.between(range.first, range.second)
            caseWhenChain = if (caseWhenChain == null) {
                CaseBuilder().`when`(condition).then(days)
            } else {
                caseWhenChain!!.`when`(condition).then(days)
            }
        }
        return caseWhenChain!!.otherwise(0)
    }

    /**
     * 푸시 알림 발송에 필요한 사용자 정보를 페이지네이션하여 한 번에 조회
     * 1. 모든 활성 사용자를 기준으로
     * 2. 각 사용자의 '숨숨 소식' 알림 설정 여부와
     * 3. 각 사용자의 '안 읽은 공지 개수'를 JOIN을 통해 한 번의 쿼리로 가져옵니다.
     */

    fun findUserNotificationPushQueryResults(pageable: Pageable): List<UserNotificationPushQueryResult> {
        // 1단계: 페이징 조건에 맞는 대상 userId 목록만 먼저 조회하는 서브쿼리
        val userIdsSubquery = queryFactory
            .select(userJpaEntity.id)
            .from(userJpaEntity)
            .where(userJpaEntity.deletedAt.isNull)
            .orderBy(userJpaEntity.id.asc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        // 2단계: 위에서 찾은 userId 목록에 대해서만 JOIN과 집계를 수행하는 메인 쿼리
        return queryFactory
            .select(
                QUserNotificationPushQueryResult(
                    userJpaEntity.id,
                    // CASE 문은 기존 로직 유지
                    CaseBuilder()
                        .`when`(userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled.isNotNull)
                        .then(
                            CaseBuilder()
                                .`when`(userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled.isTrue)
                                .then(1)
                                .otherwise(0)
                        )
                        .otherwise(1) // 설정이 없는 경우 기본값 true(1)
                        .eq(1),
                    userAnnouncementJpaEntity.count().intValue()
                )
            )
            .from(userJpaEntity)
            .leftJoin(userNotificationSettingJpaEntity)
            .on(userJpaEntity.id.eq(userNotificationSettingJpaEntity.userId))
            .leftJoin(userAnnouncementJpaEntity).on(
                userAnnouncementJpaEntity.userId.eq(userJpaEntity.id),
                userAnnouncementJpaEntity.read.isFalse, // isRead -> read
                userAnnouncementJpaEntity.deletedAt.isNull
            )
            // [핵심 개선] 조회 대상을 페이징된 userId로 한정
            .where(userJpaEntity.id.`in`(userIdsSubquery))
            .groupBy(
                userJpaEntity.id,
                userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled
            )
            .orderBy(userJpaEntity.id.asc())
            .fetch()
    }

    // BooleanBuilder를 사용하지 않고 BooleanExpression을 직접 조합하도록 수정
    private fun buildHavingExpression(
        conditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        expression: DateTimeExpression<LocalDateTime>,
    ): BooleanExpression? {
        var result: BooleanExpression? = null
        conditions.forEach { (_, range) ->
            val currentExpression = expression.between(range.first, range.second)
            // result가 null이면 현재 조건을 대입하고, null이 아니면 기존 조건에 or로 연결합니다.
            result = result?.or(currentExpression) ?: currentExpression
        }
        return result
    }
}
