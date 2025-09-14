package com.soomsoom.backend.common.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

/**
 * 단일 비즈니스 하루의 전체 정보(날짜, 시작/종료 시간)를 담는 데이터 클래스
 */
data class BusinessDay(
    val date: LocalDate,
    val start: LocalDateTime,
    val end: LocalDateTime,
)

/**
 * 조회 기간(Period)의 시작과 끝 시간을 담는 데이터 클래스
 */
data class BusinessPeriod(
    val start: LocalDateTime,
    val end: LocalDateTime,
)

@Component
class DateHelper(
    @Value("\${app.daily-cutoff-hour}")
    private val cutoffHour: Int,
) {
    private val cutoffTime: LocalTime = LocalTime.of(cutoffHour, 0)

    /**
     * 특정 시점(LocalDateTime)이 어떤 비즈니스 날짜(LocalDate)에 속하는지 계산
     */
    fun getBusinessDate(dateTime: LocalDateTime): LocalDate {
        return if (dateTime.toLocalTime().isBefore(cutoffTime)) {
            dateTime.toLocalDate().minusDays(1)
        } else {
            dateTime.toLocalDate()
        }
    }

    /**
     * [쓰기용] 특정 시점(LocalDateTime)이 속한 비즈니스 하루의 전체 정보(날짜, 시작/종료 시간)를 반환
     */
    fun getBusinessDay(dateTime: LocalDateTime): BusinessDay {
        val businessDate = getBusinessDate(dateTime)
        val start = businessDate.atTime(cutoffTime)
        val end = businessDate.plusDays(1).atTime(cutoffTime).minusNanos(1)
        return BusinessDay(businessDate, start, end)
    }

    /**
     * [읽기용 - 일별] 시작/종료 날짜(LocalDate)를 받아 조회에 사용할 시간(LocalDateTime) 범위를 반환
     * 종료일은 다음날 기준 시간 '전'까지 포함하도록 계산됩니다.
     */
    fun getBusinessPeriod(from: LocalDate, to: LocalDate): BusinessPeriod {
        val start = from.atTime(cutoffTime)
        val end = to.plusDays(1).atTime(cutoffTime)
        return BusinessPeriod(start, end)
    }

    /**
     * [읽기용 - 월별] 특정 월(YearMonth)을 받아 조회에 사용할 시간(LocalDateTime) 범위를 반환
     */
    fun getBusinessPeriod(yearMonth: YearMonth): BusinessPeriod {
        val fromDate = yearMonth.atDay(1)
        val toDate = yearMonth.atEndOfMonth()
        return getBusinessPeriod(fromDate, toDate)
    }
}
