 package com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa

 import com.querydsl.core.types.dsl.BooleanExpression
 import com.querydsl.core.types.dsl.CaseBuilder
 import com.querydsl.core.types.dsl.NumberExpression
 import com.querydsl.jpa.JPAExpressions
 import com.querydsl.jpa.impl.JPAQueryFactory
 import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity.activityCompletionLogJpaEntity
 import com.soomsoom.backend.adapter.out.persistence.diary.repository.jpa.entity.QDiaryJpaEntity.diaryJpaEntity
 import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QUserNotificationSettingJpaEntity.userNotificationSettingJpaEntity
 import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
 import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.QInactiveUserAdapterDto
 import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity.connectionLogJpaEntity
 import org.springframework.data.domain.Pageable
 import org.springframework.stereotype.Repository
 import java.time.LocalDateTime
 import java.time.LocalTime

 @Repository
 class ConnectionLogQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
 ) {
    fun existsByUserIdAndCreatedAtBetween(userId: Long, from: LocalDateTime, to: LocalDateTime): Boolean {
        val fetchFirst = queryFactory
            .selectOne()
            .from(connectionLogJpaEntity)
            .where(
                connectionLogJpaEntity.userId.eq(userId),
                connectionLogJpaEntity.createdAt.between(from, to),
                connectionLogJpaEntity.deletedAt.isNull
            )
            .fetchFirst()

        return fetchFirst != null
    }

    fun findInactiveUsers(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto> {
        if (inactivityConditions.isEmpty()) {
            return emptyList()
        }

        val lastCreatedAt = connectionLogJpaEntity.createdAt.max()

        var caseWhenChain: CaseBuilder.Cases<Int, NumberExpression<Int>>? = null
        inactivityConditions.forEach { (days, range) ->
            caseWhenChain = if (caseWhenChain == null) {
                // 첫 번째 조건으로 체인 시작
                CaseBuilder().`when`(lastCreatedAt.between(range.first, range.second)).then(days)
            } else {
                // 이후 조건들을 체인에 추가
                caseWhenChain!!.`when`(lastCreatedAt.between(range.first, range.second)).then(days)
            }
        }

        // 완성된 When 체인에 otherwise를 호출하여 최종 NumberExpression을 생성합니다.
        val inactiveDaysExpression: NumberExpression<Int> = caseWhenChain!!.otherwise(0)

        // HAVING 절: 마지막 접속 시간이 주어진 조건 범위 중 하나라도 해당하는 경우를 필터링
        var havingExpression: BooleanExpression? = null
        inactivityConditions.forEach { (_, range) ->
            val condition = lastCreatedAt.between(range.first, range.second)
            havingExpression = havingExpression?.or(condition) ?: condition
        }

        return queryFactory
            .select(
                QInactiveUserAdapterDto(
                    connectionLogJpaEntity.userId,
                    inactiveDaysExpression
                )
            )
            .from(connectionLogJpaEntity)
            .where(connectionLogJpaEntity.deletedAt.isNull)
            .groupBy(connectionLogJpaEntity.userId)
            .having(havingExpression)
            .orderBy(connectionLogJpaEntity.userId.asc())
            .offset((pageNumber * pageSize).toLong())
            .limit(pageSize.toLong())
            .fetch()
    }

     fun findDiaryReminderTargetUserIds(
         targetTime: LocalTime,
         yesterdayStart: LocalDateTime,
         yesterdayEnd: LocalDateTime,
         todayStart: LocalDateTime,
         todayEnd: LocalDateTime,
         pageable: Pageable
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
             .offset(pageable.offset)
             .limit(pageable.pageSize.toLong())
             .fetch()
     }
 }
