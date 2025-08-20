package com.soomsoom.backend.application.service.diary.query

import com.soomsoom.backend.application.port.`in`.diary.dto.EmotionStat
import com.soomsoom.backend.application.port.`in`.diary.dto.GetMonthlyDiaryStatsResult
import com.soomsoom.backend.application.port.`in`.diary.query.GetMonthlyDiaryStatsCriteria
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.GetMonthlyDiaryStatsUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetMonthlyDiaryStatsService(
    private val diaryPort: DiaryPort,
) : GetMonthlyDiaryStatsUseCase {

    @PreAuthorize("hasRole('ADMIN') or (#criteria.userId == authentication.principal.userId and #criteria.deletionStatus.name() == 'ACTIVE')")
    override fun findStats(criteria: GetMonthlyDiaryStatsCriteria): GetMonthlyDiaryStatsResult {
        val emotionCounts = diaryPort.getMonthlyEmotionCounts(criteria)

        val totalCount = emotionCounts.sumOf { it.count }

        if (totalCount == 0L) {
            return GetMonthlyDiaryStatsResult(stats = emptyList())
        }

        // 각 감정별로 정확한 백분율(소수점 포함)과 기본 정수 백분율을 계산
        val statsWithRawPercentage = emotionCounts.map {
            val rawPercentage = (it.count.toDouble() / totalCount.toDouble()) * 100.0
            Triple(it, rawPercentage, rawPercentage.toInt())
        }

        // 정수 백분율의 합계를 구해 100과의 차이를 계산
        val totalIntegerPercentage = statsWithRawPercentage.sumOf { it.third }
        var remainder = 100 - totalIntegerPercentage

        // 소수점 이하 값이 큰 순서대로 정렬
        val sortedStats = statsWithRawPercentage.sortedByDescending { it.second - it.third }

        // 최종 통계 리스트를 생성하면서, 소수점 이하 값이 컸던 항목에 나머지 값을 1씩 더함
        val finalStats = sortedStats.map { (emotionCount, _, basePercentage) ->
            val finalPercentage = if (remainder > 0) {
                remainder--
                basePercentage + 1
            } else {
                basePercentage
            }
            EmotionStat(
                emotion = emotionCount.emotion,
                count = emotionCount.count,
                percentage = finalPercentage
            )
        }
        val result = finalStats.sortedByDescending { it.count }
        return GetMonthlyDiaryStatsResult(result)
    }
}
