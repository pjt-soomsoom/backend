package com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.QInactiveUserAdapterDto
import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.QConnectionLogJpaEntity.connectionLogJpaEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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
}
