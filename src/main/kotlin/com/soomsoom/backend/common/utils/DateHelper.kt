package com.soomsoom.backend.common.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

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

    // 시간대 처리를 위한 ZoneId 상수 선언
    private val KST_ZONE = ZoneId.of("Asia/Seoul")
    private val UTC_ZONE = ZoneId.of("UTC")

    /**
     *  LocalDateTime을 UTC 기반 ZonedDateTime으로 변환하는 편의 함수
     */
    fun toZonedDateTimeInUtc(dateTime: LocalDateTime): ZonedDateTime {
        return dateTime.atZone(UTC_ZONE)
    }

    /**
     * 특정 시점(ZonedDateTime)이 KST 기준으로 어떤 비즈니스 날짜(LocalDate)에 속하는지 계산
     */
    fun getBusinessDate(dateTime: ZonedDateTime): LocalDate {
        val dateTimeInKst = dateTime.withZoneSameInstant(KST_ZONE)
        return if (dateTimeInKst.toLocalTime().isBefore(cutoffTime)) {
            dateTimeInKst.toLocalDate().minusDays(1)
        } else {
            dateTimeInKst.toLocalDate()
        }
    }

    /**
     * "지금 현재"가 속한 비즈니스 하루의 정보 반환(편의 함수)
     */
    fun getBusinessDay(): BusinessDay {
        return getBusinessDay(LocalDateTime.now())
    }

    /**
     * [쓰기용] 특정 시점(LocalDateTime)이 속한 비즈니스 하루의 전체 정보(날짜, 시작/종료 시간)를 반환
     */
    fun getBusinessDay(utcDateTime: LocalDateTime): BusinessDay {
        // 입력 받은 UTC 시간을 ZonedDateTime으로 변환
        val completedAtUtc = utcDateTime.atZone(UTC_ZONE)

        // 이 시간이 KST 기준으로 어떤 비즈니스 날짜에 속하는지 계산
        val businessDate = getBusinessDate(completedAtUtc)

        // 해당 비즈니스 날짜의 시작과 끝을 KST 기준으로 계산
        val startInKst = businessDate.atTime(cutoffTime)
        val endInKst = businessDate.plusDays(1).atTime(cutoffTime).minusNanos(1)

        // / KST 기준의 시간을 DB가 사용하는 UTC 시간으로 '번역'
        val startInUtc = ZonedDateTime.of(startInKst, KST_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime()
        val endInUtc = ZonedDateTime.of(endInKst, KST_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime()
        return BusinessDay(businessDate, startInUtc, endInUtc)
    }

    /**
     * [읽기용 - 일별] 시작/종료 날짜(LocalDate)를 받아 조회에 사용할 시간(LocalDateTime) 범위를 반환
     * 종료일은 다음날 기준 시간 '전'까지 포함하도록 계산됩니다.
     */
    fun getBusinessPeriod(from: LocalDate, to: LocalDate): BusinessPeriod {
        val startInKst = from.atTime(cutoffTime)
        val endInKst = to.plusDays(1).atTime(cutoffTime)

        val startInUtc = ZonedDateTime.of(startInKst, KST_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime()
        val endInUtc = ZonedDateTime.of(endInKst, KST_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime()
        return BusinessPeriod(startInUtc, endInUtc)
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
