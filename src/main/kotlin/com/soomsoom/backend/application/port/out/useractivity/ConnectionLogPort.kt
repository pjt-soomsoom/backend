package com.soomsoom.backend.application.port.out.useractivity

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
    fun deleteByUserId(userId: Long)
}
