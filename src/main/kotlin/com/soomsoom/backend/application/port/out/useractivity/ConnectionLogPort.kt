package com.soomsoom.backend.application.port.out.useractivity

import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.dto.InactiveUserAdapterDto
import com.soomsoom.backend.domain.useractivity.model.aggregate.ConnectionLog
import java.time.LocalDateTime

interface ConnectionLogPort {
    /**
     * 특정 사용자가 특정 비즈니스 날짜에 접속한 기록이 있는지 확인
     * @param userId 확인할 사용자의 ID
     * @param from 확인할 비즈니스 date의 시작 시간
     * @param to 확인할 비즈니스 date의 종료 시간
     * ex 8월 2일의 business 기간 = 8월 2일 06시 ~ 8월 3일 06시
     * @return 기록이 존재하면 true, 그렇지 않으면 false
     */
    fun existsByUserIdAndCreatedAtBetween(userId: Long, from: LocalDateTime, to: LocalDateTime): Boolean
    fun save(connectionLog: ConnectionLog): ConnectionLog

    /**
     * '시간 범위 조건 맵'을 기반으로, 조건에 맞는 미접속 사용자 목록과
     * 각자의 미접속 일수를 페이징하여 조회
     *
     * @param inactivityConditions Key: 미접속 일수, Value: 해당 미접속 일수에 해당하는 마지막 접속 시간 범위(from, to)
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return InactiveUserAdapterDto 목록 (userId, inactiveDays)
     */
    fun findInactiveUsers(
        inactivityConditions: Map<Int, Pair<LocalDateTime, LocalDateTime>>,
        pageNumber: Int,
        pageSize: Int,
    ): List<InactiveUserAdapterDto>
}
