package com.soomsoom.backend.application.port.`in`.activityhistory.dto

data class ActivityCompleteResult(
    val activityId: Long,
    val completionEffectTexts: List<String>,
    val rewardableMission: RewardableMissionInfo?,
) {
    /**
     * 보상 가능한 미션의 핵심 정보를 담는 중첩 DTO
     */
    data class RewardableMissionInfo(
        val missionId: Long,
        val title: String,
    )
}
