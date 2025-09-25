package com.soomsoom.backend.application.port.`in`.instructor.dto

import com.soomsoom.backend.domain.instructor.model.Instructor
import java.time.LocalDateTime

data class FindInstructorResult(
    val instructorId: Long,
    val name: String,
    val bio: String?,
    val profileImageUrl: String?,
    val isFollowing: Boolean?,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
    val rewardableMission: RewardableMissionInfo?,
) {

    /**
     * 보상 가능한 미션의 핵심 정보를 담는 중첩 DTO
     */
    data class RewardableMissionInfo(
        val missionId: Long,
        val title: String, // "야웅이와 첫 만남" 등
    )
    companion object {
        fun from(instructor: Instructor, isFollowing: Boolean?, rewardableMission: RewardableMissionInfo?): FindInstructorResult {
            return FindInstructorResult(
                instructorId = instructor.id!!,
                name = instructor.name,
                bio = instructor.bio,
                profileImageUrl = instructor.profileImageUrl,
                isFollowing = isFollowing,
                createdAt = instructor.createdAt!!,
                modifiedAt = instructor.modifiedAt,
                deletedAt = instructor.deletedAt,
                rewardableMission = rewardableMission
            )
        }
    }
}
