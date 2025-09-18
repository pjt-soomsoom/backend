package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity.activityCompletionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity.diaryJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserDeviceJpaEntity.userDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserNotificationSettingJpaEntity.userNotificationSettingJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.QInactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity.connectionLogJpaEntity
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
        return queryFactory
            .select(userNotificationSettingJpaEntity.userId)
            .from(userNotificationSettingJpaEntity)
            .where(
                // 1. [기본 조건] 알림 설정이 켜져 있고, 현재 시간과 일치하는 사용자
                userNotificationSettingJpaEntity.diaryNotificationEnabled.isTrue,
                userNotificationSettingJpaEntity.diaryNotificationTime.eq(targetTime),

                // 2. [존재 조건] 어제 접속 기록이 '존재하는' 사용자 (EXISTS)
                JPAExpressions.selectOne()
                    .from(connectionLogJpaEntity)
                    .where(
                        connectionLogJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                        connectionLogJpaEntity.createdAt.between(yesterdayStart, yesterdayEnd)
                    ).exists(),

                // 3. [미존재 조건] 오늘 일기를 쓴 기록이 '존재하지 않는' 사용자 (NOT EXISTS)
                JPAExpressions.selectOne()
                    .from(diaryJpaEntity)
                    .where(
                        diaryJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                        diaryJpaEntity.createdAt.between(todayStart, todayEnd)
                    ).notExists(),

                // 4. [미존재 조건] 오늘 활동을 완료한 기록이 '존재하지 않는' 사용자 (NOT EXISTS)
                JPAExpressions.selectOne()
                    .from(activityCompletionLogJpaEntity)
                    .where(
                        activityCompletionLogJpaEntity.userId.eq(userNotificationSettingJpaEntity.userId),
                        activityCompletionLogJpaEntity.createdAt.between(todayStart, todayEnd)
                    ).notExists()
            )
            .orderBy(userNotificationSettingJpaEntity.userId.asc()) // 페이징을 위한 정렬 추가
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
