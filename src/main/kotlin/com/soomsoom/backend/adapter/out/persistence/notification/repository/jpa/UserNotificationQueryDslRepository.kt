package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity.activityCompletionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity.diaryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity.QUserAnnouncementJpaEntity.userAnnouncementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.dto.QUserNotificationPushQueryResult
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.dto.UserNotificationPushQueryResult
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserDeviceJpaEntity.userDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserNotificationSettingJpaEntity.userNotificationSettingJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.QUserJpaEntity.userJpaEntity
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.QInactiveUserAdapterDto
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
        // 오늘 일기를 작성했는지 확인하는 서브쿼리 (Boolean)
        val hasDiaryToday = JPAExpressions.selectOne()
            .from(diaryJpaEntity)
            .where(
                diaryJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                diaryJpaEntity.createdAt.between(todayStart, todayEnd)
            ).exists()

        // 오늘 호흡 또는 명상을 했는지 확인하는 서브쿼리 (Boolean)
        val hasActivityToday = JPAExpressions.selectOne()
            .from(activityCompletionLogJpaEntity)
            .where(
                activityCompletionLogJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                activityCompletionLogJpaEntity.createdAt.between(todayStart, todayEnd),
                // ✨ 호흡 또는 명상 타입만 필터링
                activityCompletionLogJpaEntity.activityType.`in`(ActivityType.BREATHING, ActivityType.MEDITATION)
            ).exists()

        return queryFactory
            .select(userNotificationSettingJpaEntity.userId)
            .from(userNotificationSettingJpaEntity)
            .where(
                // 1. [기본 조건] 알림 설정 ON, 시간 일치
                userNotificationSettingJpaEntity.diaryNotificationEnabled.isTrue,
                userNotificationSettingJpaEntity.diaryNotificationTime.eq(targetTime),

                // 2. [접속 조건] 어제 접속 기록이 있는 사용자
                // (이 조건이 여전히 필요한지 확인이 필요합니다. 일단 유지했습니다.)
                JPAExpressions.selectOne()
                    .from(connectionLogJpaEntity)
                    .where(
                        connectionLogJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                        connectionLogJpaEntity.createdAt.between(yesterdayStart, yesterdayEnd)
                    ).exists(),

                // 3. ✨ [새로운 핵심 조건] '일기를 썼다' 와 '호흡/명상을 했다' 가 동시에 참인 경우가 아닌 사용자
                //    즉, 둘 다 한 사용자는 제외하고, 하나만 했거나 아무것도 안 한 사용자는 포함합니다.
                hasDiaryToday.and(hasActivityToday).isFalse
            )
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
        return queryFactory
            .select(
                QUserNotificationPushQueryResult(
                    userJpaEntity.id,

                    // 1. CASE 문으로 BIT 타입을 안전하게 1 또는 0으로 변환하고,
                    CaseBuilder()
                        .`when`(userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled.isNotNull)
                        .then(
                            CaseBuilder()
                                .`when`(userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled.isTrue)
                                .then(1)
                                .otherwise(0)
                        )
                        .otherwise(1)
                        .eq(1),

                    userAnnouncementJpaEntity.count().intValue()
                )
            )
            .from(userJpaEntity)
            // UserNotificationSetting 정보가 없는 사용자도 포함시키기 위해 LEFT JOIN
            .leftJoin(userNotificationSettingJpaEntity)
            .on(userJpaEntity.id.eq(userNotificationSettingJpaEntity.userId))
            // 안 읽은 공지사항을 처음부터 LEFT JOIN으로 가져옵니다.
            .leftJoin(userAnnouncementJpaEntity).on(
                userAnnouncementJpaEntity.userId.eq(userJpaEntity.id),
                userAnnouncementJpaEntity.isRead.isFalse,
                userAnnouncementJpaEntity.deletedAt.isNull
            )
            .where(userJpaEntity.deletedAt.isNull)
            // select 절에 있는 집계 함수(count) 외의 모든 컬럼을 기준으로 그룹화합니다.
            .groupBy(
                userJpaEntity.id,
                userNotificationSettingJpaEntity.soomsoomNewsNotificationEnabled
            )
            .orderBy(userJpaEntity.id.asc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
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
